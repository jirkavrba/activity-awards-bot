package dev.vrba.activityawardsbot.discord

import dev.vrba.activityawardsbot.configuration.DiscordBotConfiguration
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.hooks.EventListener
import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.api.utils.MemberCachePolicy
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class DiscordBeans(private val configuration: DiscordBotConfiguration, private val listeners: List<EventListener>) {

    @Bean
    fun client(): JDA = JDABuilder.createDefault(configuration.token)
        .addEventListeners(*listeners.toTypedArray())
        .setMemberCachePolicy(MemberCachePolicy.ALL)
        .enableIntents(GatewayIntent.GUILD_MEMBERS)
        .build()

}