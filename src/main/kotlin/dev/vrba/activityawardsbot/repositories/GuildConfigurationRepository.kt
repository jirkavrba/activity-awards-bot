package dev.vrba.activityawardsbot.repositories

import dev.vrba.activityawardsbot.entities.GuildConfiguration
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface GuildConfigurationRepository : CrudRepository<GuildConfiguration, Long>