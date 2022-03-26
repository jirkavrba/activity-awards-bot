package dev.vrba.activitybot.discord.tasks

import dev.vrba.activitybot.configuration.DiscordBotConfiguration
import dev.vrba.activitybot.configuration.GuildConfiguration
import dev.vrba.activitybot.services.MemberActivityService
import net.dv8tion.jda.api.JDA
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component
import kotlin.math.max

@Component
class AssignActivityRolesTask(
    private val service: MemberActivityService,
    private val client: JDA,
    configuration: DiscordBotConfiguration
) : CommandLineRunner {

    private val guilds: List<GuildConfiguration> = configuration.guilds

//    @Scheduled(cron = "0 0 0 * * *")
    override fun run(vararg args: String) {
        guilds.forEach { assignAwardedRoles(it) }
    }

    // TODO: Refactor this ugly-ass cancer code
    private fun assignAwardedRoles(configuration: GuildConfiguration) {
        val guild = client.getGuildById(configuration.id) ?: return
        val active = service.getMostActiveMembers(guild.idLong)
        val members = guild.members

        // Mapped role id -> member ids
        val (_, roles) = configuration.roles.sortedBy { it.order }
            .fold(Pair(0, mapOf<Long, List<Long>>())) { (start, mappedRoles), role ->
                val percent = max(members.size / 100, 1)
                val matching = active.drop(percent * start).take(role.percentage * percent)
                val mappedRole = role.id to matching

                Pair(percent + role.percentage, mappedRoles + mappedRole)
            }

        // Remove awarded roles from all previously awarded members
        configuration.roles.forEach {
            val role = guild.roles.first { role -> role.idLong == it.id }

            val new = roles[it.id] ?: return
            val old = guild.getMembersWithRoles(role).map { member -> member.idLong }

            old.forEach { member ->
                if (member !in new) {
                    guild.removeRoleFromMember(member, role).queue()
                }
            }

            new.forEach { member ->
                if (member !in old) {
                   guild.addRoleToMember(member, role).queue()
                }
            }
        }
    }
}