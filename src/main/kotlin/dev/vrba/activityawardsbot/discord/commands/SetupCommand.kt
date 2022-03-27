package dev.vrba.activityawardsbot.discord.commands

import dev.vrba.activityawardsbot.entities.GuildConfiguration
import dev.vrba.activityawardsbot.repositories.GuildConfigurationRepository
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component

@Component
class SetupCommand(private val repository: GuildConfigurationRepository) : SlashCommand {

    override val definition: SlashCommandData = Commands.slash("setup", "Setup and register this guild")
        .addOption(OptionType.CHANNEL, "announcements-channel", "Text channel, to which the awarded roles summary will be posted", true)

    override fun execute(event: SlashCommandInteractionEvent) {
        val guild = event.guild!!
        val announcementsChannel = event.getOption("announcements-channel")?.asTextChannel
            ?: throw IllegalArgumentException("Missing the announcements channel parameter")

        val interaction = event.deferReply().complete()
        val configuration = repository.findByIdOrNull(guild.idLong)
            ?: GuildConfiguration(guild.idLong, 0)

        repository.save(configuration.copy(announcementsChannelId = announcementsChannel.idLong))

        interaction.editOriginalEmbeds(setupCompleteEmbed(announcementsChannel.idLong)).queue()
    }

    private fun setupCompleteEmbed(announcementsChannelId: Long): MessageEmbed =
        EmbedBuilder()
            .setColor(0x57F287)
            .setTitle("Setup complete \uD83D\uDC4C")
            .setDescription("I will post updates to <#${announcementsChannelId}>")
            .build()
}