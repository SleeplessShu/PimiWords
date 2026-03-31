package com.sleeplessdog.pimi.games.presentation.ingameFragments

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sleeplessdog.pimi.games.presentation.interfaces.GameEvent
import com.sleeplessdog.pimi.games.presentation.interfaces.InGameLogic
import com.sleeplessdog.pimi.games.presentation.models.IngameWordsState
import com.sleeplessdog.pimi.games.presentation.models.Word
import com.sleeplessdog.pimi.utils.ShuffleFunctions
import com.sleeplessdog.pimi.utils.TimeConstants

class WordsMatchingViewModel(
    private val shuffleFunctions: ShuffleFunctions,
    private val pageSize: Int = 6,
) : ViewModel(), InGameLogic {

    override val events = MutableLiveData<GameEvent>()

    private val handler = Handler(Looper.getMainLooper())

    private var allPairs: List<Pair<Word, Word>> = emptyList()

    private var currentPage = 0
    private var currentPagePairs: List<Pair<Word, Word>> = emptyList()

    private val matchedIdsOnPage = mutableSetOf<Int>()

    private val _state = MutableLiveData(
        IngameWordsState(
            selectedWords = emptyList(),
            errorWords = emptyList(),
            correctWords = emptyList(),
            usedWords = emptyList(),
            locked = false
        )
    )
    val state: LiveData<IngameWordsState> = _state

    private val _pagePairs = MutableLiveData<List<Pair<Word, Word>>>()
    val pagePairs: LiveData<List<Pair<Word, Word>>> = _pagePairs


    override fun setPool(all: List<Pair<Word, Word>>) {
        allPairs = all
        openPage(0)
    }

    fun onWordClick(clickedWord: Word) {
        val s = _state.value ?: return
        if (s.locked) return

        val selected = s.selectedWords
        when (selected.size) {
            0 -> _state.value = s.copy(selectedWords = listOf(clickedWord))
            1 -> {
                if (clickedWord.language == selected[0].language && clickedWord.id != selected[0].id) {
                    _state.value = s.copy(selectedWords = listOf(clickedWord))
                } else if (clickedWord.id == selected[0].id && clickedWord.language == selected[0].language) {
                    _state.value = s.copy(selectedWords = emptyList())
                } else {
                    checkPair(selected[0], clickedWord)
                }
            }

            else -> _state.value = s.copy(selectedWords = emptyList())
        }
    }

    private fun openPage(page: Int) {
        if (allPairs.isEmpty()) {
            events.value = GameEvent.Completed; return
        }

        val from = page * pageSize
        if (from >= allPairs.size) {
            events.value = GameEvent.Completed; return
        }

        val to = (from + pageSize).coerceAtMost(allPairs.size)
        currentPagePairs = allPairs.subList(from, to)
        currentPage = page
        matchedIdsOnPage.clear()

        _state.value = IngameWordsState(
            selectedWords = emptyList(),
            errorWords = emptyList(),
            correctWords = emptyList(),
            usedWords = emptyList(),
            locked = false
        )
        val shuffledPairs = shuffleFunctions.shufflePairs(currentPagePairs)
        _pagePairs.value = shuffledPairs
    }


    private fun checkPair(a: Word, b: Word) {
        val s = _state.value ?: return
        val wordsIds = listOf(a.id, b.id)
        if (a.id == b.id) {
            events.value = GameEvent.Correct(wordsIds)
            val id = a.id
            matchedIdsOnPage += id

            _state.value = s.copy(
                selectedWords = emptyList(),
                errorWords = emptyList(),
                correctWords = s.correctWords + listOf(a, b),
                locked = false
            )

            handler.postDelayed({
                val cur = _state.value ?: return@postDelayed
                val newUsed = cur.usedWords.toMutableList().apply {
                    add(a); add(b)
                }
                _state.value = cur.copy(
                    correctWords = emptyList(),
                    usedWords = newUsed,
                    locked = false
                )

                if (matchedIdsOnPage.size >= currentPagePairs.size) {
                    openPage(currentPage + 1)
                }
            }, TimeConstants.REACTION)
        } else {
            events.value = GameEvent.Wrong(wordsIds)
            _state.value = s.copy(
                selectedWords = emptyList(),
                errorWords = listOf(a, b),
                locked = false
            )
            handler.postDelayed({
                val cur = _state.value ?: return@postDelayed
                _state.value = cur.copy(errorWords = emptyList(), locked = false)
            }, TimeConstants.REACTION)
        }
    }

    override fun onCleared() {
        handler.removeCallbacksAndMessages(null)
        super.onCleared()
    }
}
