package com.sleeplessdog.matchthewords.game.domain.models

import com.sleeplessdog.matchthewords.game.presentation.models.Language

data class Dictionary(
    val id: Int,
    val level: LanguageLevel,
    val category: WordsGroupsList,
    val languageOrigin: Language,
    val languageTranslate: Language,
)
