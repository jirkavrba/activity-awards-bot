package dev.vrba.activityawardsbot.repositories

import dev.vrba.activityawardsbot.entities.GuildConfiguration
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface GuildConfigurationRepository : CrudRepository<GuildConfiguration, Long> {

    @Cacheable("guild_configurations")
    fun existsByGuildId(guildId: Long): Boolean

    @CacheEvict(cacheNames = ["guild_configurations"], allEntries = true)
    override fun <S : GuildConfiguration?> save(entity: S): S

    @CacheEvict(cacheNames = ["guild_configurations"], allEntries = true)
    override fun delete(entity: GuildConfiguration)
}