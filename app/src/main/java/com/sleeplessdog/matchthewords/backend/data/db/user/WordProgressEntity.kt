package com.sleeplessdog.matchthewords.backend.data.db.user

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "word_progress")
data class WordProgressEntity(
    @PrimaryKey val wordId: Int,
    val correctCount: Int = 0,
    val wrongCount: Int = 0,
    val lastAnsweredAt: Long = 0L,
    val isLearned: Boolean = false,  // true когда correctCount >= 3
    val groupKey: String = "",
)