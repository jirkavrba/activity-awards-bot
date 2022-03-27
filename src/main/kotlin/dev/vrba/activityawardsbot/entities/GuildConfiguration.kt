package dev.vrba.activityawardsbot.entities

import org.hibernate.Hibernate
import javax.persistence.*

@Entity
@Table(name = "guild_configurations")
data class GuildConfiguration(
    @Id
    @Column(name = "guild_id", nullable = false)
    val guildId: Long,

    @Column(name = "announcements_channel_id")
    val announcementsChannelId: Long
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false

        return guildId == (other as GuildConfiguration).guildId
    }

    override fun hashCode(): Int = javaClass.hashCode()

    override fun toString(): String = this::class.simpleName + "(guildId = $guildId)"

}