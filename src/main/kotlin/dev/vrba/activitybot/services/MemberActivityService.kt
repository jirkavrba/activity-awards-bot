package dev.vrba.activitybot.services

import dev.vrba.activitybot.entities.MemberActivity
import dev.vrba.activitybot.repositories.MemberActivityRepository
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class MemberActivityService(private val repository: MemberActivityRepository) {

    fun incrementMessagesActivity(guild: Long, member: Long, date: LocalDate = LocalDate.now()) {
        // If activity for given member/guild/date does not exist, create a new one
        val activity = repository.findByGuildAndMemberAndDate(guild, member, date)
            ?: MemberActivity(
                guild = guild,
                member = member,
                date = date
            )

        repository.save(activity.copy(messages = activity.messages + 1))
    }

}