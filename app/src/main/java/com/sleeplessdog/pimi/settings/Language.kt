package com.sleeplessdog.pimi.settings

import com.sleeplessdog.pimi.games.domain.models.MutableWordBuilder
import java.util.Locale

enum class Language {
    ENGLISH,
    SPANISH,
    RUSSIAN,
    FRENCH,
    GERMAN,
    ARMENIAN,
    SERBIAN;

    fun toLocale(): Locale = when (this) {
        Language.ENGLISH -> Locale("en")
        Language.RUSSIAN -> Locale("ru")
        Language.SPANISH -> Locale("es")
        Language.FRENCH -> Locale("fr")
        Language.GERMAN -> Locale("de")
        Language.ARMENIAN -> Locale("hy")
        Language.SERBIAN -> Locale("sr")
    }

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
