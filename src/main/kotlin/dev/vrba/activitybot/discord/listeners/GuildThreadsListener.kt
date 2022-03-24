package dev.vrba.activitybot.discord.listeners

import net.dv8tion.jda.api.entities.ChannelType
import net.dv8tion.jda.api.entities.ThreadChannel
import net.dv8tion.jda.api.events.channel.ChannelCreateEvent
import net.dv8tion.jda.api.events.thread.ThreadRevealedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class GuildThreadsListener : ListenerAdapter() {

    private val logger = LoggerFactory.getLogger(this::class.qualifiedName)

    override fun onChannelCreate(event: ChannelCreateEvent) {
        val threadTypes = listOf(
            ChannelType.GUILD_PRIVATE_THREAD,
            ChannelType.GUILD_PUBLIC_THREAD,
            ChannelType.GUILD_NEWS_THREAD,
        )

        if (event.channelType in threadTypes) {
            joinThread(event.channel as ThreadChannel)
        }
    }

    override fun onThreadRevealed(event: ThreadRevealedEvent) = joinThread(event.thread)

    private fun joinThread(thread: ThreadChannel) {
        if (!thread.isJoined) {
            logger.info("Joined thread ${thread.name}")
            thread.join().queue()
        }
    }

}