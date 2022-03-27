package dev.vrba.activityawardsbot.repositories

import dev.vrba.activityawardsbot.entities.AwardRole
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface AwardRoleRepository : CrudRepository<AwardRole, UUID>