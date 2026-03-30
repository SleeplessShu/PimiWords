package com.sleeplessdog.pimi.games.presentation.ingameFragments

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sleeplessdog.pimi.games.presentation.interfaces.GameEvent
import com.sleeplessdog.pimi.games.presentation.interfaces.InGameLogic
import com.sleeplessdog.pimi.games.presentation.models.Word
import com.sleeplessdog.pimi.games.presentation.models.WriteTheWordLetterUi
import com.sleeplessdog.pimi.games.presentation.models.WriteTheWordUi
import com.sleeplessdog.pimi.utils.TimeConstants

class WriteTheWordViewModel : ViewModel(), InGameLogic {

    override val events = MutableLiveData<GameEvent>()

    private val handler = Handler(Looper.getMainLooper())

    private var pool: List<Pair<Word, Word>> = emptyList()

    private var usedIndices = mutableSetOf<Int>()

    private val _ui = MutableLiveData(WriteTheWordUi())
    val ui: LiveData<WriteTheWordUi> = _ui

    private var currentIds: List<Int> = emptyList()
    private var targetRaw = ""
    private var targetClean = ""
    private var letters: MutableList<WriteTheWordLetterUi> = mutableListOf()

    override fun setPool(pairs: List<Pair<Word, Word>>) {
        pool = pairs
        usedIndices.clear()
        nextQuestion()
    }

    private fun nextQuestion() {
        if (pool.isEmpty() || usedIndices.size >= pool.size) {
            events.value = GameEvent.Completed
            return
        }
        val available = pool.indices.filterNot { it in usedIndices }
        if (available.isEmpty()) {
            events.value = GameEvent.Completed
            return
        }

        val index = available.random()
        usedIndices += index

        val (word, translationWord) = pool[index]
        currentIds = listOf(word.id)
        createPair(prompt = word.text, translation = translationWord.text)
    }

    private fun createPair(prompt: String, translation: String) {
        targetRaw = translation
        targetClean = cleanTranslation(translation)

        letters = targetClean.mapIndexed { i, c -> WriteTheWordLetterUi(i, c) }
            .shuffled()
            .toMutableList()

        _ui.value = WriteTheWordUi(
            prompt = prompt,
            target = targetClean,
            input = "",
            letters = letters,
            locked = false,
            isCheckCorrect = false,
            isCheckEnabled = false
        )
    }

    private fun cleanTranslation(raw: String): String {
        val beforeSlash = raw.substringBefore('/')
        return beforeSlash
            .replace("\\s".toRegex(), " ")
            .replace("\u00A0", " ")
            .trim()
    }

    private val usedIndicesStack = mutableListOf<Int>()

    fun onLetterClick(position: Int) {
        val state = _ui.value ?: return
        if (state.locked) return

        val l = letters[position]
        if (l.used) return

        letters[position] = l.copy(used = true)
        usedIndicesStack.add(position)

        val newInput = state.input + l.char
        val enabled = newInput.length == targetClean.length && newInput.isNotEmpty()

        _ui.value = state.copy(
            input = state.input + l.char,
            letters = letters.toList(),
            isCheckEnabled = enabled
        )
    }

    fun onDeleteLetter() {
        val state = _ui.value ?: return
        if (state.input.isEmpty()) return

        val newInput = state.input.dropLast(1)

        if (usedIndicesStack.isNotEmpty()) {
            val lastUsedIndex = usedIndicesStack.removeAt(usedIndicesStack.lastIndex)
            letters[lastUsedIndex] = letters[lastUsedIndex].copy(used = false)
        }
        val enabled = newInput.length == targetClean.length && newInput.isNotEmpty()

        _ui.value = state.copy(
            input = newInput,
            letters = letters.toList(),
            isCheckEnabled = enabled
        )
    }

    fun onClear() {
        val state = _ui.value ?: return
        letters = letters.map { it.copy(used = false) }.toMutableList()
        usedIndicesStack.clear()
        _ui.value = state.copy(
            input = "",
            letters = letters.toList(),
            isCheckEnabled = false
        )
    }

    fun onSkip() {
        onCheck()
    }

    fun onCheck() {
        val state = _ui.value ?: return
        val ok = state.input.equals(targetClean, ignoreCase = true)

        _ui.value = state.copy(
            locked = true,
            isCheckCorrect = ok,
            isCheckEnabled = false,
            correctAnswer = if (!ok) targetClean else ""
        )
        events.value = if (ok) GameEvent.Correct(currentIds) else GameEvent.Wrong(currentIds)

        if (ok) {
            GameEvent.Correct(currentIds)
            handler.postDelayed({
                _ui.value = _ui.value?.copy(correctAnswer = "")
                nextQuestion()
            }, TimeConstants.NEXT_QUESTION)
        }
    }


    fun onNext() {
        handler.removeCallbacksAndMessages(null)
        _ui.value = _ui.value?.copy(correctAnswer = "")
        nextQuestion()
    }

    override fun onCleared() {
        handler.removeCallbacksAndMessages(null)
        super.onCleared()
    }
}
