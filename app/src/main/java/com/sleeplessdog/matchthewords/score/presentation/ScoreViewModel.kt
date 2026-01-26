package com.sleeplessdog.matchthewords.score.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sleeplessdog.matchthewords.backend.domain.models.LanguageLevel
import com.sleeplessdog.matchthewords.score.domain.models.AwardsCatalog
import com.sleeplessdog.matchthewords.score.presentation.models.ScoreUiState
import com.sleeplessdog.matchthewords.score.presentation.models.StatItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class ScoreViewModel(
    //private val scoreInteractor: ScoreInteractor,
) : ViewModel() {
    val awards = AwardsCatalog.all
    val unlocked = awards.filter { !it.isLocked }
    val locked = awards.filter { it.isLocked }
    val awardsResult = buildList {
        addAll(unlocked.take(5))
        if (size < 5) {
            addAll(locked.take(5 - size))
        }
    }
    private val _state = MutableStateFlow(
        ScoreUiState(
            level = LanguageLevel.B2,
            awards = awardsResult,
            statsWeek = StatItem(15, 3, 104, 534),
            statsAllTime = StatItem(345, 34, 304, 65534),
        )
    )
    val state: StateFlow<ScoreUiState> = _state

    fun loadData() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = false)
        }
    }
}
