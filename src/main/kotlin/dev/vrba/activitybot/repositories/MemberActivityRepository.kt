package dev.vrba.activitybot.repositories

import dev.vrba.activitybot.dtos.MemberActivitySummary
import dev.vrba.activitybot.entities.MemberActivity
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.time.LocalDate
import java.util.*

@Repository
interface MemberActivityRepository : CrudRepository<MemberActivity, UUID> {

    fun findByGuildAndMemberAndDate(guild: Long, member: Long, date: LocalDate): MemberActivity?

    @Query("""
       select new dev.vrba.activitybot.dtos.MemberActivitySummary(m.member, sum(m.messages) as messages) 
        from MemberActivity m 
        where m.guild = ?1 and m.date > ?2 
        group by m.member
    """)
    fun getActivitySummaries(guild: Long, date: LocalDate, sort: Sort): List<MemberActivitySummary>

}