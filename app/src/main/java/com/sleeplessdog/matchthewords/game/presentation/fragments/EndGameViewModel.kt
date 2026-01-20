package com.sleeplessdog.matchthewords.game.presentation.fragments

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sleeplessdog.matchthewords.game.domain.usecase.AddWordToUserDictionaryUC
import com.sleeplessdog.matchthewords.game.presentation.models.EndGameActionStatus
import com.sleeplessdog.matchthewords.game.presentation.models.EndGameWordsAction
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class EndGameViewModel(
    private val addToUserDictionaryUC: AddWordToUserDictionaryUC,
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

    /**
     * отправляет список слов с ошибками на сервер
     */
    fun sendReport() {
        val ids = _selectedWordIds.value.toList()
        if (ids.isEmpty()) return
        Log.d("DEBUG", "sendReport: $ids ОТПРАВКА ОТЧЁТА НЕ РЕАЛИЗОВАНА")
        hideActions()
    }

    /**
     * сохраняет выбранные слова в словарь пользователя
     */
    fun saveSelectedWords() {
        val ids = _selectedWordIds.value.toList()
        if (ids.isEmpty()) return
        Log.d("ENDGAMEVM", "saveSelectedWords: $ids")
        viewModelScope.launch {
            val result = addToUserDictionaryUC(ids)
            Log.d("DEBUG", "результат добавления слов в словарь $result")
        }
        hideActions()
    }

    fun hideActions() {
        _actionsWithWords.value = _actionsWithWords.value?.copy(isVisible = false)
    }
}
