package dev.vrba.activityawardsbot

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching
import org.springframework.scheduling.annotation.EnableScheduling

@EnableCaching
@EnableScheduling
@SpringBootApplication
@ConfigurationPropertiesScan(basePackages = ["dev.vrba.activityawardsbot.configuration"])
class ActivityBotApplication

fun main(args: Array<String>) {
    runApplication<ActivityBotApplication>(*args)
}
