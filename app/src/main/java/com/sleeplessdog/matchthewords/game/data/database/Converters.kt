package com.sleeplessdog.matchthewords.game.data.database

import androidx.room.TypeConverter
import com.sleeplessdog.matchthewords.game.domain.models.LanguageLevel
import com.sleeplessdog.matchthewords.game.domain.models.WordCategory

class Converters {
    @TypeConverter fun fromCategory(v: WordCategory): String = v.name
    @TypeConverter fun toCategory(v: String): WordCategory = WordCategory.valueOf(v.uppercase())

    @TypeConverter fun fromLevel(v: LanguageLevel): String = v.name
    @TypeConverter fun toLevel(v: String): LanguageLevel = LanguageLevel.valueOf(v.uppercase())
}