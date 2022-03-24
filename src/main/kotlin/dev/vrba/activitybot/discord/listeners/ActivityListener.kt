package dev.vrba.activitybot.discord.listeners

import dev.vrba.activitybot.configuration.DiscordBotConfiguration
import dev.vrba.activitybot.services.MemberActivityService
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.springframework.stereotype.Component

@Component
class ActivityListener(private val service: MemberActivityService, configuration: DiscordBotConfiguration) : ListenerAdapter() {

    private val guilds = configuration.guilds.map { it.id }

    override fun onMessageReceived(event: MessageReceivedEvent) {
        val guild = event.guild.idLong
        val member = event.author.idLong

        if (guild in guilds) {
            service.incrementMessagesActivity(guild, member)
        }
    }
}