package dev.vrba.activityawardsbot.services

import dev.vrba.activityawardsbot.entities.MemberActivity
import dev.vrba.activityawardsbot.repositories.GuildConfigurationRepository
import dev.vrba.activityawardsbot.repositories.MemberActivityRepository
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class MemberActivityService(
    private val guildsRepository: GuildConfigurationRepository,
    private val activityRepository: MemberActivityRepository
) {
    // Mapped member id -> activity
    fun getMostActiveMembers(guild: Long): List<Long> {
        val start = LocalDate.now().minusDays(7)
        val sort = Sort.by(Sort.Direction.DESC, "messages")

        return activityRepository.getActivitySummaries(guild, start, sort)
            .sortedByDescending { it.messages }
            .map { it.member }
    }

    fun incrementMessagesCount(guild: Long, member: Long, date: LocalDate = LocalDate.now()) {
        // Only collect activity in guilds with valid configuration
        if (!guildsRepository.existsByGuildId(guild)) {
            return
        }

        // If activity for given member/guild/date does not exist, create a new one
        val activity = activityRepository.findByGuildAndMemberAndDate(guild, member, date)
            ?: MemberActivity(
                guild = guild,
                member = member,
                date = date
            )

        activityRepository.save(activity.copy(messages = activity.messages + 1))
    }
}