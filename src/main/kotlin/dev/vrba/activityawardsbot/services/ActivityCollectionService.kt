package dev.vrba.activityawardsbot.services

import dev.vrba.activityawardsbot.entities.MemberActivity
import dev.vrba.activityawardsbot.repositories.GuildConfigurationRepository
import dev.vrba.activityawardsbot.repositories.MemberActivityRepository
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.springframework.data.domain.Sort
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class ActivityCollectionService(
    private val guildsRepository: GuildConfigurationRepository,
    private val activityRepository: MemberActivityRepository
) : ListenerAdapter() {

    // Mapped member id -> activity
    fun getMostActiveMembers(guild: Long): List<Long> {
        val start = LocalDate.now().minusDays(7)
        val sort = Sort.by(Sort.Direction.DESC, "messages")

        return activityRepository.getActivitySummaries(guild, start, sort)
            .sortedByDescending { it.messages }
            .map { it.member }
    }

    override fun onMessageReceived(event: MessageReceivedEvent) {
        val guild = event.guild.idLong
        val member = event.author.idLong
        val date = LocalDate.now()

        val configuration = guildsRepository.findByIdOrNull(guild) ?: return

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