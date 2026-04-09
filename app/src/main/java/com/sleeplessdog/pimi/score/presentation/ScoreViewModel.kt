package com.sleeplessdog.pimi.score.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sleeplessdog.pimi.score.GetScoreUiStateUC
import com.sleeplessdog.pimi.score.presentation.models.ScoreUiState
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
            repeat(3) { attempt ->
                try {
                    _state.value = getScoreUiState()
                    return@launch
                } catch (e: android.database.sqlite.SQLiteDiskIOException) {
                    if (attempt < 2) kotlinx.coroutines.delay(300L * (attempt + 1))
                }
            }
        }
    }
}
