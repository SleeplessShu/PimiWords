package com.sleeplessdog.matchthewords.game.presentation.models

import com.sleeplessdog.matchthewords.backend.domain.models.LanguageLevel
import com.sleeplessdog.matchthewords.backend.domain.models.WordsGroupsList

data class GameSettings(
    val language1: Language = Language.RUSSIAN,
    val language2: Language = Language.SPANISH,
    val level: Set<LanguageLevel> = setOf(LanguageLevel.A1),
    val category: Set<WordsGroupsList> = setOf(WordsGroupsList.RANDOM),
    val difficult: DifficultLevel = DifficultLevel.MEDIUM,
)

