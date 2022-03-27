package dev.vrba.activityawardsbot.discord.listeners

import dev.vrba.activityawardsbot.configuration.DiscordBotConfiguration
import dev.vrba.activityawardsbot.discord.commands.SlashCommand
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.events.ReadyEvent
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.slf4j.LoggerFactory
import org.springframework.core.env.Environment
import org.springframework.core.env.Profiles
import org.springframework.stereotype.Component

@Component
class SlashCommandListener(
    private val commands: List<SlashCommand>,
    private val environment: Environment,
    private val configuration: DiscordBotConfiguration,
    private val client: JDA
) : ListenerAdapter() {

    private val logger = LoggerFactory.getLogger(this::class.qualifiedName)

    override fun onReady(event: ReadyEvent) {
        val definitions = commands.map { it.definition }

        if (environment.acceptsProfiles(Profiles.of("development"))) {
            val id = configuration.developmentGuildId
            val guild = client.getGuildById(id)
                ?: throw IllegalStateException("Cannot find the configured development guild [$id]")

            return guild.updateCommands()
                .addCommands(definitions)
                .queue()
        }

        client.updateCommands()
            .addCommands(definitions)
            .queue()
    }

    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
        event.guild
            ?.retrieveMember(event.user)
            ?.complete()
            ?.hasPermission(Permission.MANAGE_SERVER)
            ?: return event.replyEmbeds(administratorsOnlyEmbed()).queue()

        val handler = commands.firstOrNull { it.definition.name == event.name }
            ?: return logger.error("Cannot find handler for /${event.name}")

        try {
            handler.execute(event)
        }
        catch (exception: Exception) {
            val embed = exceptionEmbed()

            if (event.isAcknowledged) {
                return event.interaction.replyEmbeds(embed).queue()
            }

            event.replyEmbeds(embed).queue()
        }
    }

    private fun administratorsOnlyEmbed(): MessageEmbed =
        EmbedBuilder()
            .setTitle("Sorry, this command is limited to guild administrators only")
            .setColor(0xED4245)
            .build()

    private fun exceptionEmbed(): MessageEmbed =
        EmbedBuilder()
            .setTitle("Sorry, there was an error")
            .setColor(0xED4245)
            .build()
}