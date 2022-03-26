package dev.vrba.activitybot.services

import dev.vrba.activitybot.entities.MemberActivity
import dev.vrba.activitybot.repositories.MemberActivityRepository
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import java.time.Duration
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

    // Mapped member id -> activity
    fun getMostActiveMembers(guild: Long): List<Long> {
        val start = LocalDate.now().minusDays(7)
        val sort = Sort.by(Sort.Direction.DESC, "messages")

        return repository.getActivitySummaries(guild, start, sort)
            .sortedByDescending { it.messages }
            .map { it.member }
    }
}