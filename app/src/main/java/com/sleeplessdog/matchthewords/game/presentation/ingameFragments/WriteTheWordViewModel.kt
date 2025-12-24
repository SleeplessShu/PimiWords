package com.sleeplessdog.matchthewords.game.presentation.ingameFragments

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sleeplessdog.matchthewords.game.presentation.interfaces.GameEvent
import com.sleeplessdog.matchthewords.game.presentation.interfaces.InGameLogic
import com.sleeplessdog.matchthewords.game.presentation.models.Word
import com.sleeplessdog.matchthewords.game.presentation.models.WriteTheWordLetterUi
import com.sleeplessdog.matchthewords.game.presentation.models.WriteTheWordUi
import com.sleeplessdog.matchthewords.utils.TimeConstants

class WriteTheWordViewModel : ViewModel(), InGameLogic {

    // === контракт игр: только GameEvent наружу ===
    override val events = MutableLiveData<GameEvent>()

    private val handler = Handler(Looper.getMainLooper())

    private var pool: List<Pair<Word, Word>> = emptyList()

    private var usedIndices = mutableSetOf<Int>()

    private val _ui = MutableLiveData(WriteTheWordUi())
    val ui: LiveData<WriteTheWordUi> = _ui

    // текущие данные вопроса
    private var currentIds: List<Int> = emptyList()
    private var targetRaw = ""          // исходный перевод (как пришёл)
    private var targetClean = ""        // часть ДО '/' (обрезанная)
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

        // генерим буквы именно из "чистой" строки ДО слеша
        letters = targetClean.mapIndexed { i, c -> WriteTheWordLetterUi(i, c) }
            .shuffled()
            .toMutableList()

        _ui.value = WriteTheWordUi(
            prompt = prompt,
            target = targetClean,            // показываем и в UI, чтобы было видно "эталон" (если нужно)
            input = "",
            letters = letters,
            locked = false,
            isCheckCorrect = false,   // сброс цвета, не показываем подсветку с прошлого слова
            isCheckEnabled = false
        )
    }

    /** оставляем часть ДО '/', плюс trim */
    private fun cleanTranslation(raw: String): String {
        val beforeSlash = raw.substringBefore('/')
        return beforeSlash.trim()
    }

    private val usedIndicesStack = mutableListOf<Int>()

    fun onLetterClick(position: Int) {
        val state = _ui.value ?: return
        if (state.locked) return

        val l = letters[position]
        if (l.used) return               // уже использована — игнор

        letters[position] = l.copy(used = true)
        usedIndicesStack.add(position)   // сохраняем позицию

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

    /*fun onBackspace() {
        val state = _ui.value ?: return
        if (state.locked || state.input.isEmpty()) return

        // снимаем used с последней добавленной буквы по символу
        val lastChar = state.input.last()
        val idx = letters.indexOfLast { it.used && it.char == lastChar }
        if (idx != -1) {
            letters[idx] = letters[idx].copy(used = false)
            _ui.value = state.copy(
                input = state.input.dropLast(1),
                letters = letters.toList()
            )
        }
    }*/

    fun onClear() {
        val state = _ui.value ?: return
        letters = letters.map { it.copy(used = false) }.toMutableList()
        _ui.value = state.copy(
            input = "",
            letters = letters.toList(),
            isCheckEnabled = false
        )
    }

    fun onCheck() {
        val state = _ui.value ?: return

        // сравниваем с targetClean (часть ДО '/'), регистр не важен
        val ok = state.input.equals(targetClean, ignoreCase = true)

        _ui.value = state.copy(
            locked = true,
            isCheckCorrect = ok,
            isCheckEnabled = false
        )
        events.value = if (ok) GameEvent.Correct(currentIds) else GameEvent.Wrong(currentIds)
        handler.postDelayed({ nextQuestion() }, TimeConstants.NEXT_QUESTION)
    }

    override fun onCleared() {
        handler.removeCallbacksAndMessages(null)
        super.onCleared()
    }
}
