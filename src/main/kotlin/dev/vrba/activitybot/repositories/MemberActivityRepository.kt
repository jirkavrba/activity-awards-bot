package dev.vrba.activitybot.repositories

import dev.vrba.activitybot.entities.MemberActivity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.time.LocalDate
import java.util.UUID

@Repository
interface MemberActivityRepository : CrudRepository<MemberActivity, UUID> {

    fun findByGuildAndMemberAndDate(guild: Long, member: Long, date: LocalDate): MemberActivity?

}