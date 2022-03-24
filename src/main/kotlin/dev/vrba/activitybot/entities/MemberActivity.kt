package dev.vrba.activitybot.entities

import java.time.LocalDate
import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Index
import javax.persistence.Table

@Entity
@Table(
    name = "member_activity",
    indexes = [
        Index(name = "IX_member_guild_date", columnList = "guild_id,member_id,date", unique = true)
    ]
)
class MemberActivity(
    @Id
    val id: UUID = UUID.randomUUID(),

    @Column(name = "member_id", nullable = false)
    val member: Long,

    @Column(name = "guild_id", nullable = false)
    val guild: Long,

    @Column(nullable = false)
    val messages: Long = 0,

    @Column(nullable = false)
    val date: LocalDate
)