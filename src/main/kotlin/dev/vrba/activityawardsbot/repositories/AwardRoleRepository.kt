package dev.vrba.activityawardsbot.repositories

import dev.vrba.activityawardsbot.entities.AwardRole
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface AwardRoleRepository : CrudRepository<AwardRole, UUID> {

    fun countByGuildId(guildId: Long): Long

    fun findAllByGuildId(guildId: Long, sort: Sort): List<AwardRole>

    fun findByGuildIdAndRoleId(guildId: Long, roleId: Long): AwardRole?

    @Query("select a from AwardRole a where a.guildId = ?1 and a.ordinal > ?2")
    fun findAllByGuildIdThatNeedOrdinalShift(guildId: Long, ordinal: Int): List<AwardRole>
}