package dev.vrba.activityawardsbot.discord.listeners

import dev.vrba.activityawardsbot.services.MemberActivityService
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.springframework.stereotype.Component
import java.time.LocalDate

@Component
class ActivityCollectionListener(private val service: MemberActivityService) : ListenerAdapter() {

    override fun onMessageReceived(event: MessageReceivedEvent) {
        val guild = event.guild.idLong
        val member = event.author.idLong
        val date = LocalDate.now()

        service.incrementMessagesCount(guild, member, date)
    }

}
