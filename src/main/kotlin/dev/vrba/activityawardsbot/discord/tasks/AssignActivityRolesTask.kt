package dev.vrba.activityawardsbot.discord.tasks

import dev.vrba.activityawardsbot.configuration.DiscordBotConfiguration
import dev.vrba.activityawardsbot.configuration.GuildConfiguration
import dev.vrba.activityawardsbot.configuration.RoleConfiguration
import dev.vrba.activityawardsbot.services.MemberActivityService
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.MessageEmbed
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component
import java.time.Instant
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
        val members = guild.loadMembers().get()

        // Mapped role id -> member ids
        val (_, roles) = configuration.roles
            .sortedBy { it.order }
            .fold(Pair(0, mapOf<Long, List<Long>>())) { (start, mappedRoles), role ->
                val percent = max(members.size / 100, 1)
                val matching = active.drop(percent * start).take(role.percentage * percent)
                val mappedRole = role.id to matching

                Pair(percent + role.percentage, mappedRoles + mappedRole)
            }

        // Remove awarded roles from all previously awarded members
        val awarded = configuration.roles.map {
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

            return@map it to new
        }

        val embed = buildAnnouncementEmbed(awarded)
        val channel = guild.getTextChannelById(configuration.announcementChannelId)
            ?: throw IllegalStateException("Cannot find the configured announcements channel")

        return channel.sendMessageEmbeds(embed).queue()
    }

    private fun buildAnnouncementEmbed(awarded: List<Pair<RoleConfiguration, List<Long>>>): MessageEmbed {
        val description = awarded
            .sortedBy { it.first.order }
            .fold("") {
                builder, (role, members) -> builder +
                    "**${role.order}.** <@&${role.id}>\n" +
                    members.joinToString("\n") { "<@${it}>" } +
                    "\n\n"
            }

        // TODO: Add localization?
        return EmbedBuilder()
            .setColor(0xFEE75C)
            .setTitle("Awarded roles update")
            .setDescription(description)
            .setTimestamp(Instant.now())
            .build()
    }
}