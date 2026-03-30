package com.sleeplessdog.pimi.score.presentation.models

import com.sleeplessdog.pimi.settings.LanguageLevel
import com.sleeplessdog.pimi.score.models.AwardMeta

data class ScoreUiState(
    val level: LanguageLevel = LanguageLevel.A1,
    val statsWeek: StatItem = StatItem(),
    val statsAllTime: StatItem = StatItem(),
    val awards: List<AwardMeta> = emptyList(),
    val isLoading: Boolean = false,
)