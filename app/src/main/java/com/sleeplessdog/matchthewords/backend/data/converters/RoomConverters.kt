package com.sleeplessdog.matchthewords.backend.data.converters

import androidx.room.TypeConverter
import com.sleeplessdog.matchthewords.backend.domain.models.DifficultyLevel
import com.sleeplessdog.matchthewords.backend.domain.models.LanguageLevel
import com.sleeplessdog.matchthewords.score.domain.models.AwardId
import java.time.LocalDate

class RoomConverters {
    @TypeConverter
    fun fromDifficulty(value: DifficultyLevel): String = value.name

    @TypeConverter
    fun toDifficulty(value: String): DifficultyLevel = enumValueOf(value)

    @TypeConverter
    fun fromLanguageLevel(value: LanguageLevel): String = value.name

    @TypeConverter
    fun toLanguageLevel(value: String): LanguageLevel = enumValueOf(value)

    @TypeConverter
    fun fromAwardId(value: AwardId): String = value.name

    @TypeConverter
    fun toAwardId(value: String): AwardId = enumValueOf(value)

    @TypeConverter
    fun fromStringList(value: List<String>): String = value.joinToString(",")

    @TypeConverter
    fun toStringList(value: String): List<String> =
        if (value.isEmpty()) emptyList() else value.split(",")

    @TypeConverter
    fun fromDate(value: LocalDate): Long = value.toEpochDay()

    @TypeConverter
    fun toDate(value: Long): LocalDate = LocalDate.ofEpochDay(value)
}
