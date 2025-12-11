package com.sleeplessdog.matchthewords.game.presentation.ingameFragments

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sleeplessdog.matchthewords.game.presentation.interfaces.GameEvent
import com.sleeplessdog.matchthewords.game.presentation.interfaces.InGameLogic
import com.sleeplessdog.matchthewords.game.presentation.models.ButtonState
import com.sleeplessdog.matchthewords.game.presentation.models.GameUiOOF
import com.sleeplessdog.matchthewords.game.presentation.models.OneOfFourQuestion
import com.sleeplessdog.matchthewords.game.presentation.models.Word
import com.sleeplessdog.matchthewords.utils.ConstantsConditions.DEFAULT_QUESTION_SEQUENCE
import com.sleeplessdog.matchthewords.utils.ConstantsConditions.MAX_BUTTON_INDEX
import com.sleeplessdog.matchthewords.utils.ConstantsConditions.MIN_BUTTON_INDEX
import com.sleeplessdog.matchthewords.utils.ConstantsConditions.MIN_VARIANTS_COUNT_OOF
import com.sleeplessdog.matchthewords.utils.ConstantsConditions.ONE_OF_FOUR_SET
import com.sleeplessdog.matchthewords.utils.ConstantsTimeReaction
import com.sleeplessdog.matchthewords.utils.ShuffleFunctions
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private data class ClickContext(
    val buttonIndex: Int,
    val question: OneOfFourQuestion,
    val picked: Word,
    val isCorrect: Boolean,
    val wordsIds: List<Int>
)

class OneOfFourViewModel(
    val shuffleFunctions: ShuffleFunctions
) : ViewModel(), InGameLogic {

    private val handler = Handler(Looper.getMainLooper())

    override val events = MutableLiveData<GameEvent>()

    // Пул доступных пар: всё, что использовали в вопросе (база + 3 ложных), выбывает
    private val questions = mutableListOf<Pair<Word, Word>>()

    // UI
    private val _ui = MutableLiveData(GameUiOOF())
    val ui: LiveData<GameUiOOF> get() = _ui

    // текущее состояние вопроса
    private var current: OneOfFourQuestion? = null

    // защита от отложенных коллбеков после перехода к новому вопросу
    private var questionSeq: Int = DEFAULT_QUESTION_SEQUENCE

    override fun setPool(pairs: List<Pair<Word, Word>>) {
        questions.clear()
        questions.addAll(pairs)
        nextQuestion()
    }

    fun onAnswerClick(buttonIndex: Int) {
        val ctx = buildClickContext(buttonIndex) ?: return

        if (ctx.isCorrect) {
            reactOnCorrect(
                wordsIds = ctx.wordsIds, buttonIndex = ctx.buttonIndex, question = ctx.question
            )
        } else {
            reactOnWrong(
                wordsIds = ctx.wordsIds, buttonIndex = ctx.buttonIndex
            )
        }
    }

    private fun buildClickContext(buttonIndex: Int): ClickContext? {

        _ui.value?.let { state ->
            current?.let { q ->

                if (state.locked || buttonIndex !in MIN_BUTTON_INDEX..MAX_BUTTON_INDEX) {
                    return null
                }

                val picked = q.optionsSecond.getOrNull(buttonIndex)
                val btnState = state.states.getOrNull(buttonIndex)

                if (picked != null && btnState != null && btnState.enabled) {

                    val isCorrect = picked.id == q.correctSecondId
                    return ClickContext(
                        buttonIndex = buttonIndex,
                        question = q,
                        picked = picked,
                        isCorrect = isCorrect,
                        wordsIds = listOf(picked.id, q.correctSecondId)
                    )
                }
            }
        }

        return null
    }

    private fun reactOnCorrect(wordsIds: List<Int>, buttonIndex: Int, question: OneOfFourQuestion) {
        events.value = GameEvent.Correct(wordsIds)
        paintAndLock(buttonIndex)
        val seq = questionSeq

        viewModelScope.launch {
            delay(ConstantsTimeReaction.REACTION)
            if (questionSeq == seq) {
                consumeAndNext(question)
            }
        }
    }

    private fun reactOnWrong(wordsIds: List<Int>, buttonIndex: Int) {
        events.value = GameEvent.Wrong(wordsIds)
        val newStates = (_ui.value?.states ?: List(MIN_VARIANTS_COUNT_OOF) { ButtonState.DEFAULT }).toMutableList()
        newStates[buttonIndex] = ButtonState.ERROR
        _ui.value = _ui.value?.copy(states = newStates)
        val seq = questionSeq

        viewModelScope.launch {
            delay(ConstantsTimeReaction.REACTION)

            if (questionSeq != seq) return@launch

            val cur = _ui.value ?: return@launch
            val st = cur.states.toMutableList()

            if (!cur.locked && st.getOrNull(buttonIndex) == ButtonState.ERROR) {
                st[buttonIndex] = ButtonState.DISABLED
                _ui.value = cur.copy(states = st)
            }
        }
    }

    private fun nextQuestion() {
        val built = shuffleFunctions.makeOneOfFourQuestion(questions)
        if (built == null) {
            events.value = GameEvent.Completed
            current = null
            return
        }
        current = built
        questionSeq++

        _ui.value = GameUiOOF(
            originalText = built.originalFirst.text,
            options = built.optionsSecond.map { it.text },
            states = List(ONE_OF_FOUR_SET) { ButtonState.DEFAULT },
            locked = false
        )
    }

    private fun paintAndLock(correctIndex: Int) {
        val st = MutableList(ONE_OF_FOUR_SET) { ButtonState.DISABLED }
        st[correctIndex] = ButtonState.CORRECT
        _ui.value = _ui.value?.copy(states = st, locked = true)
    }

    private fun consumeAndNext(q: OneOfFourQuestion) {
        if (q.consumedFirstIds.isNotEmpty()) {
            questions.removeAll { q.consumedFirstIds.contains(it.first.id) }
        }
        current = null
        nextQuestion()
    }

    override fun onCleared() {
        handler.removeCallbacksAndMessages(null)
        super.onCleared()
    }
}
