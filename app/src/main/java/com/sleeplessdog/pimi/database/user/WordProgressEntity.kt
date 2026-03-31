package com.sleeplessdog.pimi.database.user

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "word_progress")
data class WordProgressEntity(
    @PrimaryKey val wordId: Int,
    val correctCount: Int = 0,
    val wrongCount: Int = 0,
    val lastAnsweredAt: Long = 0L,
    val isLearned: Boolean = false,
    val groupKey: String = "",
)