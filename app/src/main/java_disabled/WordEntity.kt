package com.sleeplessdog.pimi.game.data

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
    @PrimaryKey val id: Int? = null,        // в БД notNull=false (даже при PK)
    val english: String,                    // NOT NULL
    val spanish: String?,                   // NULLABLE
    val russian: String?,                   // NULLABLE
    val french: String?,                    // NULLABLE
    val german: String?,                    // NULLABLE
    val level: String,                      // NOT NULL
    val category: String?,                  // NULLABLE
    @ColumnInfo(defaultValue = "0")
    val correct: Int? = 0,                  // NULLABLE в БД, default 0
    @ColumnInfo(defaultValue = "0")
    val mistake: Int? = 0,                  // NULLABLE в БД, default 0
    val date: String?,                      // NULLABLE (в БД notNull=false)
    @ColumnInfo(name = "subtype")
    val subtype: String?,
)