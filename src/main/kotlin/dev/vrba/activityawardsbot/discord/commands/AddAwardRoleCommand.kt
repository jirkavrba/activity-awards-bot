package dev.vrba.activityawardsbot.discord.commands

import dev.vrba.activityawardsbot.entities.AwardRole
import dev.vrba.activityawardsbot.repositories.AwardRoleRepository
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData
import org.springframework.stereotype.Component

@Component
class AddAwardRoleCommand(private val repository: AwardRoleRepository) : SlashCommand {

    override val definition: SlashCommandData = Commands.slash("add-award-role", "Add a new award role binding")
        .addOption(OptionType.ROLE, "role", "Role that should be awarded to the selected % of most active users", true)
        .addOption(OptionType.INTEGER, "percentage", "Number of % that this role should be awarded to", true)

    override fun execute(event: SlashCommandInteractionEvent) {
        val role = event.getOption("role")?.asRole ?: throw IllegalArgumentException("Missing the role parameter")
        val percentage = event.getOption("percentage")?.asInt ?: throw IllegalArgumentException("Missing the percentage parameter")
        val guild = event.guild ?: throw IllegalStateException("Command invoked outside of a guild")

        val interaction = event.deferReply().complete()

        val ordinal = repository.countByGuildId(guild.idLong).toInt() + 1
        val entity = AwardRole(
            guildId = guild.idLong,
            roleId = role.idLong,
            ordinal = ordinal,
            percentage = percentage
        )

        repository.save(entity)

        interaction.editOriginalEmbeds(awardRoleEmbed(entity)).queue()
    }

    private fun awardRoleEmbed(entity: AwardRole): MessageEmbed =
        EmbedBuilder()
            .setTitle("New award role added")
            .setColor(0x5865F2)
            .addField("Role", "<@&${entity.roleId}>", false)
            .addField("Applies to", "Top ${entity.percentage}% most active users after ${entity.ordinal - 1} other awards", false)
            .build()
}