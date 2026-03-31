package com.sleeplessdog.pimi.endGame

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sleeplessdog.pimi.games.domain.usecases.AddWordToUserDictionaryUC
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class EndGameViewModel(
    private val addWordToUserDictionary: AddWordToUserDictionaryUC,
    private val reportWordMistake: ReportWordMistakeUC,
) : ViewModel() {
    private val _actionsWithWords = MutableLiveData<EndGameActionStatus>()
    val actionsWithWords: LiveData<EndGameActionStatus> = _actionsWithWords

    private val _selectedWordIds = MutableStateFlow<Set<Int>>(emptySet())
    val selectedWordIds: StateFlow<Set<Int>> = _selectedWordIds

    val isActionEnabled: StateFlow<Boolean> =
        _selectedWordIds
            .map { it.isNotEmpty() }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = false
            )


    fun updateSelection(ids: List<Int>) {
        _selectedWordIds.value = ids.toSet()
    }

    fun reportAboutMistake() {
        _actionsWithWords.value = EndGameActionStatus(
            isVisible = true, action = EndGameWordsAction.REPORT_ABOUT_MISTAKE
        )
    }

    fun saveWordsToUsersDictionary() {
        _actionsWithWords.value = EndGameActionStatus(
            isVisible = true, action = EndGameWordsAction.SAVE_WORDS_TO_USERS_DICTIONARY
        )
    }

    fun sendReport() {
        val ids = _selectedWordIds.value.toList()
        if (ids.isEmpty()) return
        viewModelScope.launch {
            reportWordMistake(ids)
        }
        hideActions()
    }

    fun saveSelectedWords() {
        val ids = _selectedWordIds.value.toList()
        if (ids.isEmpty()) return
        viewModelScope.launch {
            addWordToUserDictionary(ids)
        }
        hideActions()
    }

    fun hideActions() {
        _actionsWithWords.value = _actionsWithWords.value?.copy(isVisible = false)
    }
}
