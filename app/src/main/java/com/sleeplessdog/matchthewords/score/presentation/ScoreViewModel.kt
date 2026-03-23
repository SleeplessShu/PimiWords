package com.sleeplessdog.matchthewords.score.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sleeplessdog.matchthewords.backend.domain.usecases.GetScoreUiStateUC
import com.sleeplessdog.matchthewords.score.presentation.models.ScoreUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class ScoreViewModel(
    private val getScoreUiState: GetScoreUiStateUC,
) : ViewModel() {

    private val _state = MutableStateFlow(ScoreUiState())
    val state: StateFlow<ScoreUiState> = _state

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            _state.value = getScoreUiState()
        }
    }
}
