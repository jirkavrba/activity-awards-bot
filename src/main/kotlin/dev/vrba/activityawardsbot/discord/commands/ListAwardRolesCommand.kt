package dev.vrba.activityawardsbot.discord.commands

import dev.vrba.activityawardsbot.entities.AwardRole
import dev.vrba.activityawardsbot.repositories.AwardRoleRepository
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Component

@Component
class ListAwardRolesCommand(private val repository: AwardRoleRepository) : SlashCommand {

    override val definition: SlashCommandData = Commands.slash("list", "Lists all award roles registered for this guild")

    override fun execute(event: SlashCommandInteractionEvent) {
        val guild = event.guild ?: throw IllegalStateException("Command invoked outside of a guild")

        val interaction = event.deferReply().complete()

        val roles = repository.findAllByGuildId(guild.idLong, Sort.by("ordinal"))
        val embed = awardRolesEmbed(roles)

        return interaction.editOriginalEmbeds(embed).queue()
    }

    private fun awardRolesEmbed(roles: List<AwardRole>): MessageEmbed {
        val description = roles.joinToString("\n") {
            "**${it.ordinal}**: <@&${it.roleId}> (+${it.percentage}%)"
        }

        return EmbedBuilder()
            .setColor(0x5865F2)
            .setTitle("Award roles registered for this guild")
            .setDescription(description)
            .build()
    }
}