package dev.vrba.activitybot.entities

import org.hibernate.Hibernate
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
data class MemberActivity(
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
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false

        return id == (other as MemberActivity).id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    override fun toString(): String = this::class.simpleName + "(id = $id)"
}