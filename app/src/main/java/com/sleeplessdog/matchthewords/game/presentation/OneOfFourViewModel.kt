package com.sleeplessdog.matchthewords.game.presentation

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sleeplessdog.matchthewords.game.presentation.models.AnswerEvent
import com.sleeplessdog.matchthewords.game.presentation.models.ButtonState
import com.sleeplessdog.matchthewords.game.presentation.models.Word
import kotlin.collections.toMutableList

class OneOfFourViewModel : ViewModel() {


    data class Ui(
        val originalText: String = "",
        val options: List<String> = listOf("", "", "", ""),
        val states: List<ButtonState> = List(4) { ButtonState.DEFAULT },
        val locked: Boolean = false
    )

    private val _answerEvents = MutableLiveData<AnswerEvent>()
    val answerEvents: LiveData<AnswerEvent> get() = _answerEvents

    private val handler = Handler(Looper.getMainLooper())

    private var pool: List<Pair<Word, Word>> = emptyList() // (original, translation)
    private var index = 0
    private var currentCorrectIndex = -1
    private var translationsPool: List<Word> = emptyList() // все переводы (for wrong options)
    private var poolKey: String? = null

    private val _ui = MutableLiveData(Ui())
    val ui: LiveData<Ui> get() = _ui

    private val _completed = MutableLiveData(false)
    val completed: LiveData<Boolean> get() = _completed

    /** Подать пачку пар для генерации 1/4 сетов. */
    fun setPool(pairs: List<Pair<Word, Word>>) {
        // создаём уникальный ключ для списка (например, по id первой пары + размер)
        val newKey = "${pairs.size}:${pairs.firstOrNull()?.first?.id ?: -1}"
        if (newKey == poolKey) return

        poolKey = newKey
        pool = pairs
        translationsPool = pairs.map { it.second }
        index = 0
        _completed.value = false
        nextQuestion()
    }

    fun onAnswerClick(buttonIndex: Int) {
        val state = _ui.value ?: return
        if (state.locked) return

        if (buttonIndex == currentCorrectIndex) {
            //_answerEvents.value = AnswerEvent.CORRECT
            paintAndLock(correctIndex = buttonIndex)

            handler.postDelayed({ proceedAfterCorrect() }, 1000L)

        } else {
            //_answerEvents.value = AnswerEvent.WRONG
            val newStates = state.states.toMutableList()
            newStates[buttonIndex] = ButtonState.ERROR

            _ui.value = state.copy(states = newStates)

        }
    }

    private fun proceedAfterCorrect() {
        index++
        if (index >= pool.size) {
            _completed.value = true
        } else {
            nextQuestion()
        }
    }

    private fun nextQuestion() {
        if (pool.isEmpty() || index !in pool.indices) {
            _completed.value = true
            return
        }
        val (original, correct) = pool[index]

        // генерим 3 неправильных перевода
        val wrongs = translationsPool
            .filter { it.id != correct.id }
            .shuffled()
            .take(3)

        // собираем 4 опции, перемешиваем
        val options = (wrongs + correct).shuffled()

        currentCorrectIndex = options.indexOfFirst { it.id == correct.id }

        _ui.value = Ui(
            originalText = original.text,
            options = options.map { it.text },
            states = List(4) { ButtonState.DEFAULT },
            locked = false
        )
    }

    private fun paintAndLock(correctIndex: Int) {
        val st = MutableList(4) { ButtonState.DISABLED }
        st[correctIndex] = ButtonState.CORRECT
        _ui.value = _ui.value?.copy(states = st, locked = true)
    }

    override fun onCleared() {
        handler.removeCallbacksAndMessages(null)
        super.onCleared()
    }

    /*
    Родитель уже грузит wordsPairs постранично. Чайлд слушает wordsPairs и из каждой страницы генерит 6 вопросов 1-из-4 (по одному на каждую пару).

На каждый неверный клик — только эта кнопка красится word_background_error и дизейблится.

На верный — окраска word_background_correct на 1 сек, затем следующий сет.

Когда все пары страницы пройдены → completed = true → вызываем parentViewModel.onGameEnd() (или, если хочешь продолжать следующую страницу, вместо onGameEnd() зови parentViewModel.onPageCompleted() — скажи, поменяю).

Если хочешь, добавлю вариант, где мы берём «неправильные варианты» не только из текущей страницы, а из всего пула слов (чтобы разнообразнее).
     */

}
