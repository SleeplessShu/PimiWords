package com.sleeplessdog.matchthewords.backend.data.db.user

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "UserWordProgress")
data class UserWordProgressEntity(
    @PrimaryKey val globalId: Long,
    val correctCount: Int = 0,
    val mistakeCount: Int = 0,
    val lastSeenAt: Long = 0L,
    val isLearned: Boolean = false,
)


