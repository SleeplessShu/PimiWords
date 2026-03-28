package com.sleeplessdog.pimi.database.user

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.sleeplessdog.pimi.score.domain.models.AwardId

@Entity(tableName = "awards")
data class AwardEntity(
    @PrimaryKey val id: AwardId,
    val unlocked: Boolean,
    val unlockedAt: Long? = null,
)
