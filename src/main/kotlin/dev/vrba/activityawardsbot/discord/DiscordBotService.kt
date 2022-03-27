package dev.vrba.activityawardsbot.discord

import dev.vrba.activityawardsbot.configuration.DiscordBotConfiguration
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.hooks.EventListener
import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.api.utils.MemberCachePolicy
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Service

@Service
class DiscordBotService(
    private val configuration: DiscordBotConfiguration,
    private val listeners: List<EventListener>
) : CommandLineRunner {

    @get:Bean
    val client = JDABuilder.createDefault(configuration.token)
        .setMemberCachePolicy(MemberCachePolicy.ALL)
        .enableIntents(GatewayIntent.GUILD_MEMBERS)
        .addEventListeners(*listeners.toTypedArray())
        .build()
        .awaitReady()

    override fun run(vararg args: String) {

        client.presence.activity = Activity.watching("your activity")
    }

}