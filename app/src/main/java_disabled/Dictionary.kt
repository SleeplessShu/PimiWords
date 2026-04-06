package com.sleeplessdog.pimi.game.domain.models

import com.sleeplessdog.pimi.game.presentation.models.Language

data class Dictionary(
    val id: Int,
    val level: LanguageLevel,
    val category: WordsGroupsList,
    val languageOrigin: Language,
    val languageTranslate: Language,
)
