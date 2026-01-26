package com.sleeplessdog.matchthewords.backend.data.db.global

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.sleeplessdog.matchthewords.backend.domain.models.LanguageLevel

@Entity(tableName = "GlobalDictionary")
data class GlobalDictionaryEntity(
    @PrimaryKey
    val id: Long,
    val groupKey: String,
    val subGroupKey: String?,
    val difficulty: LanguageLevel,
    val isDeleted: Boolean,
    val english: String,
    val spanish: String?,
    val russian: String?,
    val french: String?,
    val german: String?,
    val armenian: String?,
    val serbian: String?,
)

