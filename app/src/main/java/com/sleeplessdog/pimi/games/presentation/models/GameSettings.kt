package com.sleeplessdog.pimi.games.presentation.models

import com.sleeplessdog.pimi.games.domain.models.WordsGroupsList
import com.sleeplessdog.pimi.settings.DifficultyLevel
import com.sleeplessdog.pimi.settings.Language
import com.sleeplessdog.pimi.settings.LanguageLevel

data class GameSettings(
    val language1: Language = Language.RUSSIAN,
    val language2: Language = Language.SPANISH,
    val level: Set<LanguageLevel> = setOf(LanguageLevel.A1),
    val category: Set<WordsGroupsList> = setOf(WordsGroupsList.RANDOM),
    val difficult: DifficultyLevel = DifficultyLevel.MEDIUM,
)

