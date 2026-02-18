package com.sleeplessdog.matchthewords.game.presentation.models

import com.sleeplessdog.matchthewords.backend.domain.models.MutableWordBuilder

enum class Language {
    ENGLISH,
    SPANISH,
    RUSSIAN,
    FRENCH,
    GERMAN,
    ARMENIAN,
    SERBIAN;

    fun apply(value: String, entity: MutableWordBuilder) {
        when (this) {
            ENGLISH -> entity.english = value
            SPANISH -> entity.spanish = value
            RUSSIAN -> entity.russian = value
            FRENCH -> entity.french = value
            GERMAN -> entity.german = value
            ARMENIAN -> entity.armenian = value
            SERBIAN -> entity.serbian = value
        }
    }
}
