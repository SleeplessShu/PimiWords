package com.sleeplessdog.matchthewords.backend.domain.models

import com.sleeplessdog.matchthewords.game.presentation.models.Language

class MutableWordBuilder {
    var english: String? = null
    var spanish: String? = null
    var russian: String? = null
    var french: String? = null
    var german: String? = null
    var armenian: String? = null
    var serbian: String? = null
}

fun MutableWordBuilder.set(language: Language, value: String) {
    when (language) {
        Language.ENGLISH -> english = value
        Language.SPANISH -> spanish = value
        Language.RUSSIAN -> russian = value
        Language.FRENCH -> french = value
        Language.GERMAN -> german = value
        Language.ARMENIAN -> armenian = value
        Language.SERBIAN -> serbian = value
    }
}