package com.sleeplessdog.matchthewords.backend.domain.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "UserSettings")
data class UserSettingsEntity(
    @PrimaryKey
    val id: Int = 1, // всегда одна строка

    val languageLevels: String,        // "A1,A2,B1"
    val selectedGroups: String, // "animals,food,saved_words"
)
