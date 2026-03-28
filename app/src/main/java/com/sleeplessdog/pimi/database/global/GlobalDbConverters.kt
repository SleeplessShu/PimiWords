package com.sleeplessdog.pimi.database.global

import androidx.room.TypeConverter
import com.sleeplessdog.pimi.settings.LanguageLevel

class GlobalDbConverters {

    @TypeConverter
    fun fromLanguageLevel(value: LanguageLevel): String = value.name

    @TypeConverter
    fun toLanguageLevel(value: String): LanguageLevel =
        LanguageLevel.valueOf(value)

    @TypeConverter
    fun fromBoolean(value: Boolean): Int = if (value) 1 else 0

    @TypeConverter
    fun toBoolean(value: Int): Boolean = value != 0
}
