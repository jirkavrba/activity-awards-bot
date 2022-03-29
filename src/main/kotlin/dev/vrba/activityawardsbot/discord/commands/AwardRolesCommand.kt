package dev.vrba.activityawardsbot.discord.commands

import dev.vrba.activityawardsbot.discord.tasks.RoleAwardingTask
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData
import org.springframework.stereotype.Component

@Component
class AwardRolesCommand(private val task: RoleAwardingTask) : SlashCommand {

    override val definition: SlashCommandData = Commands.slash("award", "Manually trigger the role awarding task")

    override fun execute(event: SlashCommandInteractionEvent) {
        val interaction = event.deferReply().complete()

        task.updateAwardRoles()

        val embed = EmbedBuilder()
                .setColor(0x57F287)
                .setTitle("Role awarding task was executed")
                .build()

        interaction.editOriginalEmbeds(embed).queue()
    }
}