package dev.vrba.activityawardsbot.entities

import org.hibernate.Hibernate
import java.util.UUID
import javax.persistence.*

@Entity
@Table(name = "award_roles", uniqueConstraints = [
    UniqueConstraint(name = "UK_guild_id_ordinal", columnNames = ["guild_id", "ordinal"])
])
data class AwardRole(
    @Id
    val id: UUID = UUID.randomUUID(),

    @Column(name = "guild_id", nullable = false)
    val guildId: Long,

    @Column(name = "role_id", nullable = false)
    val roleId: Long,

    @Column(name = "ordinal", nullable = false)
    val ordinal: Int,

    @Column(name = "percentage", nullable = false)
    val percentage: Int
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false

        return id == (other as AwardRole).id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    override fun toString(): String = this::class.simpleName + "(id = $id)"
}