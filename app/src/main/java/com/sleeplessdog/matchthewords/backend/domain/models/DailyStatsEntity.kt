package com.sleeplessdog.matchthewords.backend.domain.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "DailyStats")
data class DailyStatsEntity(
    @PrimaryKey
    val date: String, // yyyy-MM-dd

    val points: Int = 0,
    val gamesPlayed: Int = 0,
    val wordsCorrect: Int = 0,
    val wordsMistake: Int = 0,
    val timeSpentMs: Long = 0,
)