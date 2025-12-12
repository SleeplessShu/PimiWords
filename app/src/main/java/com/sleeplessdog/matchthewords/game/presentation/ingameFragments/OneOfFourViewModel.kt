package com.sleeplessdog.matchthewords.game.presentation.ingameFragments

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sleeplessdog.matchthewords.game.presentation.controller.OneOfFourClickHelper
import com.sleeplessdog.matchthewords.game.presentation.interfaces.GameEvent
import com.sleeplessdog.matchthewords.game.presentation.interfaces.InGameLogic
import com.sleeplessdog.matchthewords.game.presentation.models.ButtonState
import com.sleeplessdog.matchthewords.game.presentation.models.GameUiOOF
import com.sleeplessdog.matchthewords.game.presentation.models.OneOfFourQuestion
import com.sleeplessdog.matchthewords.game.presentation.models.Word
import com.sleeplessdog.matchthewords.utils.ConstantsConditions.DEFAULT_QUESTION_SEQUENCE
import com.sleeplessdog.matchthewords.utils.ConstantsConditions.MIN_VARIANTS_COUNT_OOF
import com.sleeplessdog.matchthewords.utils.ConstantsConditions.ONE_OF_FOUR_SET
import com.sleeplessdog.matchthewords.utils.ConstantsTimeReaction
import com.sleeplessdog.matchthewords.utils.ShuffleFunctions
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class ClickContext(
    val buttonIndex: Int,
    val question: OneOfFourQuestion,
    val picked: Word,
    val isCorrect: Boolean,
    val wordsIds: List<Int>
)

class OneOfFourViewModel(
    val shuffleFunctions: ShuffleFunctions
) : ViewModel(), InGameLogic {

    private val clickHelper = OneOfFourClickHelper()

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
        val ctx = clickHelper.processClick(_ui.value, current, buttonIndex) ?: return

        if (ctx.isCorrect) {
            reactOnCorrect(ctx)
        } else {
            reactOnWrong(ctx)
        }
    }

    private fun reactOnCorrect(ctx: ClickContext) {
        events.value = GameEvent.Correct(ctx.wordsIds)
        paintAndLock(ctx.buttonIndex)
        val seq = questionSeq

        viewModelScope.launch {
            delay(ConstantsTimeReaction.REACTION)
            if (questionSeq == seq) {
                consumeAndNext(ctx.question)
            }
        }
    }

    private fun reactOnWrong(ctx: ClickContext) {
        events.value = GameEvent.Wrong(ctx.wordsIds)
        val newStates = (_ui.value?.states ?: List(MIN_VARIANTS_COUNT_OOF) {
            ButtonState.DEFAULT
        }).toMutableList()
        newStates[ctx.buttonIndex] = ButtonState.ERROR
        _ui.value = _ui.value?.copy(states = newStates)
        val seq = questionSeq

        viewModelScope.launch {
            delay(ConstantsTimeReaction.REACTION)

            if (questionSeq != seq) return@launch

            val cur = _ui.value ?: return@launch
            val st = cur.states.toMutableList()

            if (!cur.locked && st.getOrNull(ctx.buttonIndex) == ButtonState.ERROR) {
                st[ctx.buttonIndex] = ButtonState.DISABLED
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
