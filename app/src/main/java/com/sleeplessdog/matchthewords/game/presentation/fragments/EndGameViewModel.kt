package com.sleeplessdog.matchthewords.game.presentation.fragments

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sleeplessdog.matchthewords.game.domain.usecase.AddWordToUserDictionaryUC
import com.sleeplessdog.matchthewords.game.presentation.models.EndGameActionStatus
import com.sleeplessdog.matchthewords.game.presentation.models.EndGameWordsAction
import com.sleeplessdog.matchthewords.game.presentation.models.Word
import kotlinx.coroutines.launch

class EndGameViewModel(
    private val addToUserDictionaryUC: AddWordToUserDictionaryUC,
) : ViewModel() {
    private val _actionsWithWords = MutableLiveData<EndGameActionStatus>()
    val actionsWithWords: LiveData<EndGameActionStatus> = _actionsWithWords

    private var selectedPairs: List<Pair<Word, Word>> = emptyList()

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


    fun updateSelectedPairs(newPairs: List<Pair<Word, Word>>) {
        selectedPairs = newPairs
    }

    fun sendReport() {
        hideActions()
        Log.d("DEBUG", "sendReport: $selectedPairs")
    }

    fun saveSelectedWords() {
        hideActions()

        val wordsIds: Set<Int> = selectedPairs.flatMap { pair ->
            listOf(pair.first.id, pair.second.id)
        }.toSet()

        viewModelScope.launch {
            addToUserDictionaryUC(wordsIds)

        }
    }

    fun hideActions() {
        _actionsWithWords.value = _actionsWithWords.value?.copy(isVisible = false)
    }
}
