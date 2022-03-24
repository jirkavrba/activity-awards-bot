package dev.vrba.activitybot.discord

import dev.vrba.activitybot.configuration.DiscordBotConfiguration
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.hooks.EventListener
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Service

@Service
class DiscordBotService(
    private val configuration: DiscordBotConfiguration,
    private val listeners: List<EventListener>
) : CommandLineRunner {

    override fun run(vararg args: String) {
        val client = JDABuilder.createDefault(configuration.token)
            .addEventListeners(*listeners.toTypedArray())
            .build()
            .awaitReady()

        client.presence.activity = Activity.watching("your activity")
    }

}