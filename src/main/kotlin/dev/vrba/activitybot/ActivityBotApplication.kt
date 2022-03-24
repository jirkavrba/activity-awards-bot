package dev.vrba.activitybot

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ActivityBotApplication

fun main(args: Array<String>) {
    runApplication<ActivityBotApplication>(*args)
}
