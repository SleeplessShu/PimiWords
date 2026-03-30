package com.sleeplessdog.pimi.database.user

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "session_log")
data class SessionLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: String,            // "2025-03-21"
    val durationMinutes: Int,
    val wordsCount: Int,
    val isCompleted: Boolean = true,
)