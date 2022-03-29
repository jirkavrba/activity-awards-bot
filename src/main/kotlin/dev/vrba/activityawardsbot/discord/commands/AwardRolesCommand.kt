package dev.vrba.activityawardsbot.discord.commands

import dev.vrba.activityawardsbot.discord.tasks.RoleAwardingTask
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData
import org.springframework.scheduling.TaskScheduler
import org.springframework.stereotype.Component
import java.time.Duration
import java.time.Instant
import org.springframework.context.annotation.Lazy as SpringLazy

@Component
class AwardRolesCommand @SpringLazy constructor(
        private val task: RoleAwardingTask,
        private val scheduler: TaskScheduler
) : SlashCommand {

    override val definition: SlashCommandData = Commands.slash("award", "Manually trigger the role awarding task")

    override fun execute(event: SlashCommandInteractionEvent) {
        val interaction = event.deferReply().complete()

        scheduler.schedule({ task.updateAwardRoles() }, Instant.now() + Duration.ofSeconds(5))

        val embed = EmbedBuilder()
                .setColor(0x57F287)
                .setTitle("Role awarding task was executed")
                .build()

        interaction.editOriginalEmbeds(embed).queue()
    }
}