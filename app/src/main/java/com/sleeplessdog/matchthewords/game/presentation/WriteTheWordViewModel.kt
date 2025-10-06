package com.sleeplessdog.matchthewords.game.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sleeplessdog.matchthewords.game.presentation.models.AnswerEvent
import com.sleeplessdog.matchthewords.game.presentation.models.WriteTheWordLetterUi
import com.sleeplessdog.matchthewords.game.presentation.models.WriteTheWordUi
import kotlin.collections.indexOfLast
import kotlin.collections.map
import kotlin.collections.toList

class WriteTheWordViewModel : ViewModel() {
    private val _ui = MutableLiveData(WriteTheWordUi())
    val ui: LiveData<WriteTheWordUi> = _ui

    private val _events = MutableLiveData<Boolean>()
    val events: LiveData<Boolean> = _events

    // текущие данные раунда
    private var target = ""
    private var letters: MutableList<WriteTheWordLetterUi> = mutableListOf()

    fun createPair(prompt: String, translation: String) {
        target = translation
        letters = translation.mapIndexed { i, c -> WriteTheWordLetterUi(i, c) }
            .shuffled()
            .toMutableList()
        _ui.value = WriteTheWordUi(
            prompt = prompt,
            target = target,
            input = "",
            letters = letters,
            locked = false
        )
    }

    fun onLetterClick(position: Int) {
        val state = _ui.value ?: return
        if (state.locked) return
        val l = letters[position]
        if (l.used) return
        letters[position] = l.copy(used = true)
        _ui.value = state.copy(
            input = state.input + l.char,
            letters = letters.toList()
        )
    }

    fun onBackspace() {
        val state = _ui.value ?: return
        if (state.locked || state.input.isEmpty()) return

        // снимаем used с последней добавленной буквы в исходном порядке:
        val lastChar = state.input.last()
        // ищем первую used-букву справа налево, совпадающую по символу
        val idx = letters.indexOfLast { it.used && it.char == lastChar }
        if (idx != -1) {
            letters[idx] = letters[idx].copy(used = false)
            _ui.value = state.copy(
                input = state.input.dropLast(1),
                letters = letters.toList()
            )
        }
    }

    fun onClear() {
        val state = _ui.value ?: return
        letters = letters.map { it.copy(used = false) }.toMutableList()
        _ui.value = state.copy(input = "", letters = letters.toList())
    }

    fun onCheck() {
        val state = _ui.value ?: return
        val ok = state.input.equals(target, ignoreCase = false)
        _ui.value = state.copy(locked = true)
        _events.value = if (ok) true else false
    }

    fun next() { // вызвать из фрагмента после анимации/задержки
        _ui.value = _ui.value?.copy(locked = false)
    }
}
