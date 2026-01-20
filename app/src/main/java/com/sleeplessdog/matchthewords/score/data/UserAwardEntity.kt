package com.sleeplessdog.matchthewords.score.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_awards")
data class UserAwardEntity(

    @PrimaryKey
    val awardId: String,

    val unlocked: Boolean,

    val unlockedAt: Long?,
)