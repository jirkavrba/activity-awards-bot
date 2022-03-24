package dev.vrba.activitybot

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication
@ConfigurationPropertiesScan(basePackages = ["dev.vrba.activitybot.configuration"])
class ActivityBotApplication

fun main(args: Array<String>) {
    runApplication<ActivityBotApplication>(*args)
}
