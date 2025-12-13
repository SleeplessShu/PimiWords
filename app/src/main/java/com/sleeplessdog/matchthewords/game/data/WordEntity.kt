package com.sleeplessdog.matchthewords.game.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "words",
    indices = [
        Index(name = "idx_words_english", value = ["english"]),
        Index(name = "idx_words_level", value = ["level"])
    ]
)
data class WordEntity(
    @PrimaryKey val id: Int? = null,
    val english: String,
    val spanish: String?,
    val russian: String?,
    val french: String?,
    val german: String?,
    val level: String,
    val category: String?,
    @ColumnInfo(defaultValue = "0")
    val correct: Int? = 0,
    @ColumnInfo(defaultValue = "0")
    val mistake: Int? = 0,
    val date: String?,
    @ColumnInfo(name = "subtype")
    val subtype: String?,
)
