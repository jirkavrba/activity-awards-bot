package dev.vrba.activityawardsbot

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@EnableScheduling
@SpringBootApplication
@ConfigurationPropertiesScan(basePackages = ["dev.vrba.activityawardsbot.configuration"])
class ActivityBotApplication

fun main(args: Array<String>) {
    runApplication<ActivityBotApplication>(*args)
}
