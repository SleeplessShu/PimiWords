package com.sleeplessdog.pimi.games.presentation.ingameFragments

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sleeplessdog.pimi.games.presentation.interfaces.GameEvent
import com.sleeplessdog.pimi.games.presentation.interfaces.InGameLogic
import com.sleeplessdog.pimi.games.presentation.models.ButtonState
import com.sleeplessdog.pimi.games.presentation.models.GameUiOOF
import com.sleeplessdog.pimi.games.presentation.models.OneOfFourQuestion
import com.sleeplessdog.pimi.games.presentation.models.Word
import com.sleeplessdog.pimi.utils.ShuffleFunctions
import com.sleeplessdog.pimi.utils.TimeConstants

class OneOfFourViewModel(
    val shuffleFunctions: ShuffleFunctions,
) : ViewModel(), InGameLogic {

    private val handler = Handler(Looper.getMainLooper())

    override val events = MutableLiveData<GameEvent>()

    private val questions = mutableListOf<Pair<Word, Word>>()

    private val _ui = MutableLiveData(GameUiOOF())
    val ui: LiveData<GameUiOOF> get() = _ui

    private var current: OneOfFourQuestion? = null

    private var questionSeq: Int = 0

    override fun setPool(pairs: List<Pair<Word, Word>>) {
        questions.clear()
        questions.addAll(pairs)
        nextQuestion()
    }

    fun onAnswerClick(buttonIndex: Int) {
        val state = _ui.value ?: return
        if (state.locked) return
        if (buttonIndex !in 0..3) return

        val btnState = state.states.getOrNull(buttonIndex) ?: ButtonState.DEFAULT
        if (!btnState.enabled) return

        val q = current ?: return
        val picked = q.optionsSecond.getOrNull(buttonIndex) ?: return
        val isCorrect = picked.id == q.correctSecondId
        val wordsIds = listOf(picked.id, q.correctSecondId)
        if (isCorrect) {
            events.value = GameEvent.Correct(wordsIds)
            paintAndLock(buttonIndex)
            val seq = questionSeq
            handler.postDelayed({
                if (questionSeq == seq) {
                    consumeAndNext(q)
                }
            }, TimeConstants.REACTION)
        } else {
            events.value = GameEvent.Wrong(wordsIds)
            val newStates = (_ui.value?.states ?: List(4) { ButtonState.DEFAULT }).toMutableList()
            newStates[buttonIndex] = ButtonState.ERROR
            _ui.value = _ui.value?.copy(states = newStates)

            val seq = questionSeq
            handler.postDelayed({
                if (questionSeq != seq) return@postDelayed
                val cur = _ui.value ?: return@postDelayed
                val st = cur.states.toMutableList()
                if (!cur.locked && st.getOrNull(buttonIndex) == ButtonState.ERROR) {
                    st[buttonIndex] = ButtonState.DISABLED
                    _ui.value = cur.copy(states = st)
                }
            }, TimeConstants.DISABLE)
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
            states = List(4) { ButtonState.DEFAULT },
            locked = false
        )
    }

    private fun paintAndLock(correctIndex: Int) {
        val st = MutableList(4) { ButtonState.DISABLED }
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
