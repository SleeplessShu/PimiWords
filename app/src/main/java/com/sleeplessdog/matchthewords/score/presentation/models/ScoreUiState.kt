package com.sleeplessdog.matchthewords.score.presentation.models

import com.sleeplessdog.matchthewords.backend.domain.models.LanguageLevel
import com.sleeplessdog.matchthewords.score.models.AwardMeta

data class ScoreUiState(
    val level: LanguageLevel = LanguageLevel.A1,
    val statsWeek: StatItem = StatItem(),
    val statsAllTime: StatItem = StatItem(),
    val awards: List<AwardMeta> = emptyList(),
    val isLoading: Boolean = false,
)