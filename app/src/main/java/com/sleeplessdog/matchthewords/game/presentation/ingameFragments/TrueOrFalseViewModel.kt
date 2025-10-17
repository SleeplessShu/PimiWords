package com.sleeplessdog.matchthewords.game.presentation.ingameFragments

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sleeplessdog.matchthewords.game.presentation.interfaces.GameEvent
import com.sleeplessdog.matchthewords.game.presentation.interfaces.InGameLogic
import com.sleeplessdog.matchthewords.game.presentation.models.TfQuestionUi
import com.sleeplessdog.matchthewords.game.presentation.models.Word
import com.sleeplessdog.matchthewords.utils.ShuffleFunctions
import com.sleeplessdog.matchthewords.utils.TimeReactionConstants
import kotlin.collections.getOrNull

class TrueOrFalseViewModel (
    private val shuffleFunctions: ShuffleFunctions
) : ViewModel(), InGameLogic {

    override val events = MutableLiveData<GameEvent>()

    private val handler = Handler(Looper.getMainLooper())

    private var questions: List<TfQuestionUi> = emptyList()
    private var index = 0

    private val _ui = MutableLiveData<TfQuestionUi?>()
    val ui: LiveData<TfQuestionUi?> = _ui


    override fun setPool(pairs: List<Pair<Word, Word>>) {
        // готовим набор 1-разовых вопросов
        questions = shuffleFunctions.buildTrueFalseSetOnce(
            pairs,
            shuffleQuestionsOrder = true
        )
        index = 0
        nextQuestion()
    }

    fun onTrueClicked() = answer(true)
    fun onFalseClicked() = answer(false)

    private fun answer(userThinksTrue: Boolean) {
        val q = questions.getOrNull(index - 1) ?: return
        val wordsIds = listOf(q.word.id, q.shownTranslation.id)
        val ok = userThinksTrue == q.isCorrect

        events.value = if (ok) GameEvent.Correct(wordsIds) else GameEvent.Wrong(wordsIds)
        _ui.value = _ui.value?.copy(locked = true)

        handler.postDelayed({ nextQuestion() }, TimeReactionConstants.NEXT_QUESTION)
    }

    private fun nextQuestion() {
        if (index >= questions.size) {
            _ui.value?.copy(locked = true)
            events.value = GameEvent.Completed
            return
        }

        val q = questions[index++]
        _ui.value = q.copy(locked = false)
    }

    override fun onCleared() {
        handler.removeCallbacksAndMessages(null)
        super.onCleared()
    }
}