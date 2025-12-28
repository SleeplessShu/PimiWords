package com.sleeplessdog.matchthewords.game.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_dictionary")
data class UserWordEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val english: String?,
    val spanish: String?,
    val russian: String?,
    val french: String?,
    val german: String?,
    val category: String?,
    val notes: String? = null,
    @ColumnInfo(defaultValue = "0")
    val correct: Int? = 0,
    @ColumnInfo(defaultValue = "0")
    val mistake: Int? = 0,
    val dateLastSeen: String?,
    val dateAdded: String?,
)