package dev.vrba.activityawardsbot.discord.tasks

import dev.vrba.activityawardsbot.entities.AwardRole
import dev.vrba.activityawardsbot.entities.GuildConfiguration
import dev.vrba.activityawardsbot.repositories.AwardRoleRepository
import dev.vrba.activityawardsbot.repositories.GuildConfigurationRepository
import dev.vrba.activityawardsbot.services.MemberActivityService
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.MessageEmbed
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Sort
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.Instant
import kotlin.math.max

@Component
class RoleAwardingTask(
    private val guildRepository: GuildConfigurationRepository,
    private val roleRepository: AwardRoleRepository,
    private val service: MemberActivityService,
    private val client: JDA
) {

    private val logger = LoggerFactory.getLogger(this::class.qualifiedName)

    // TODO: Add cron expression
    @Scheduled(initialDelay = 1000, fixedRate = 120_000_000)
    fun updateAwardRoles() {
        guildRepository.findAll().forEach {
            try {
                updateAwardRoles(it)
            }
            catch (exception: Exception) {
                logger.error("Updating award roles raised an exception", exception)
            }
        }
    }

    private fun updateAwardRoles(configuration: GuildConfiguration) {
        val guild = client.getGuildById(configuration.guildId) ?: throw IllegalStateException("Cannot find guild [${configuration.guildId}]")

        val roles = roleRepository.findAllByGuildId(configuration.guildId, Sort.by("ordinal"))
        val members = guild.loadMembers().get()

        val percent = max(members.size / 100, 1)

        val mostActive = service.getMostActiveMembers(configuration.guildId)
        val initial = Pair(mostActive, emptyMap<AwardRole, List<Long>>())

        val (_, mappings) = roles.fold(initial) { (remaining, mapped), award ->
            val count = percent * award.percentage
            val selected = remaining.take(count)
            val role = guild.getRoleById(award.roleId) ?: throw IllegalStateException("Cannot find the configured award role [${award.roleId}]")

            val remove = members.filter { it.roles.contains(role) && it.idLong !in selected }
            val add = members.filter { !it.roles.contains(role) && it.idLong in selected }

            remove.forEach { guild.removeRoleFromMember(it, role).queue() }
            add.forEach { guild.addRoleToMember(it, role).queue() }

            Pair(remaining.drop(count), mapped + (award to selected))
        }

        val embed = createAwardRolesEmbed(guild, mappings)
        val channel = client.getTextChannelById(configuration.announcementsChannelId)
            ?: throw IllegalStateException("Cannot find the configured announcement channel [${configuration.announcementsChannelId}]")


        channel.sendMessageEmbeds(embed).queue()
    }

    private fun createAwardRolesEmbed(guild: Guild, mappings: Map<AwardRole, List<Long>>): MessageEmbed {
        val builder = EmbedBuilder()
            .setTitle(guild.name)
            .setThumbnail(guild.iconUrl)
            .setTimestamp(Instant.now())

        return mappings.entries
            .sortedBy { it.key.ordinal }
            .fold(builder) { embed, (award, members) ->
                val role = guild.roles.first { it.idLong == award.roleId }
                val list = members.joinToString("\n") { "<@$it>" }

                embed.addField(role.name, list, false)
            }
            .build()
    }
}