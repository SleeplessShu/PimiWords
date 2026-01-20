package com.sleeplessdog.matchthewords.score.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.sleeplessdog.matchthewords.score.domain.models.AwardId

@Entity(tableName = "awards")
data class AwardEntity(
    @PrimaryKey val id: AwardId,
    val unlocked: Boolean,
    val unlockedAt: Long? = null,
)
