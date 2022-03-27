package dev.vrba.activityawardsbot.configuration

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties(prefix = "discord")
class DiscordBotConfiguration(
    val token: String,
    val developmentGuildId: Long
)