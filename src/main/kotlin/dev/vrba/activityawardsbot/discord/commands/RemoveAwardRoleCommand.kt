package dev.vrba.activityawardsbot.discord.commands

import dev.vrba.activityawardsbot.repositories.AwardRoleRepository
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData
import org.springframework.stereotype.Component

@Component
class RemoveAwardRoleCommand(private val repository: AwardRoleRepository) : SlashCommand {

    override val definition: SlashCommandData = Commands.slash("remove-award-role", "Remove award role bindings for the selected role")
        .addOption(OptionType.ROLE, "role", "Role that should be awarded to the selected % of most active users", true)

    override fun execute(event: SlashCommandInteractionEvent) {
        val role = event.getOption("role")?.asRole ?: throw IllegalArgumentException("Missing the role parameter")
        val guild = event.guild ?: throw IllegalStateException("Command invoked outside of a guild")

        val interaction = event.deferReply().complete()

        val entity = repository.findByGuildIdAndRoleId(guild.idLong, role.idLong)
            ?: return interaction.editOriginalEmbeds(awardRoleNotFoundEmbed()).queue()

        val shifted = repository.findAllByGuildIdThatNeedOrdinalShift(guild.idLong, entity.ordinal).map {
            it.copy(ordinal = it.ordinal - 1)
        }

        repository.delete(entity)
        repository.saveAll(shifted)

        interaction.editOriginalEmbeds(awardRoleRemovedEmbed()).queue()
    }

    private fun awardRoleNotFoundEmbed(): MessageEmbed =
        EmbedBuilder()
            .setTitle("No matching award role found")
            .setColor(0xED4245)
            .build()

    private fun awardRoleRemovedEmbed(): MessageEmbed =
        EmbedBuilder()
            .setTitle("Award role removed")
            .setColor(0x5865F2)
            .build()
}