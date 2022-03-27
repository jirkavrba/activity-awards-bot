package dev.vrba.activityawardsbot.discord

import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.hooks.EventListener
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Service

@Service
class DiscordBotService(private val client: JDA, private val listeners: List<EventListener>) : CommandLineRunner {

    override fun run(vararg args: String) {
        client.addEventListener(*listeners.toTypedArray())
        client.presence.activity = Activity.watching("your activity")
    }

}