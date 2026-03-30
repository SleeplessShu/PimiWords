package com.sleeplessdog.pimi.database.user

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_stats")
data class UserStatsEntity(
    @PrimaryKey val id: Int = 1,
    val totalWordsLearned: Int = 0,
    val totalGamesPlayed: Int = 0,
    val totalScores: Int = 0,
    val weekWordsLearned: Int = 0,
    val weekGamesPlayed: Int = 0,
    val weekScores: Int = 0,
    val weekStartTimestamp: Long = 0L,
    val currentStreak: Int = 0,
    val lastPlayedDate: String = "", // "2025-03-21"
    val totalSessionMinutes: Int = 0,
)