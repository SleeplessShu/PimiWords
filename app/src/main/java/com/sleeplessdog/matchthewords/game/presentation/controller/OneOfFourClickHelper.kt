package com.sleeplessdog.matchthewords.game.presentation.controller

import com.sleeplessdog.matchthewords.game.presentation.ingameFragments.ClickContext
import com.sleeplessdog.matchthewords.game.presentation.models.GameUiOOF
import com.sleeplessdog.matchthewords.game.presentation.models.OneOfFourQuestion
import com.sleeplessdog.matchthewords.utils.ConstantsConditions.MAX_BUTTON_INDEX
import com.sleeplessdog.matchthewords.utils.ConstantsConditions.MIN_BUTTON_INDEX

class OneOfFourClickHelper {

    fun processClick(
        state: GameUiOOF?, question: OneOfFourQuestion?, buttonIndex: Int
    ): ClickContext? {

        if (!isGameContextValid(state, question)) {
            return null
        }

        val safeState = state!!
        val safeQuestion = question!!

        val isInteractable = isButtonInteractable(safeState, safeQuestion, buttonIndex)

        return if (isInteractable) {
            createClickContext(safeQuestion, buttonIndex)
        } else {
            null
        }
    }

    private fun isGameContextValid(state: GameUiOOF?, question: OneOfFourQuestion?): Boolean {
        return state != null && question != null
    }

    private fun isButtonInteractable(
        state: GameUiOOF, question: OneOfFourQuestion, index: Int
    ): Boolean {
        var conditionsPassed = true

        if (!canClick(state, index)) {
            conditionsPassed = false
        }

        val btnState = state.states.getOrNull(index)
        val wordExists = question.optionsSecond.getOrNull(index) != null

        if (btnState == null || !btnState.enabled) {
            conditionsPassed = false
        }

        return if (conditionsPassed) {
            wordExists
        } else {
            false
        }
    }

    private fun createClickContext(
        question: OneOfFourQuestion, index: Int
    ): ClickContext {
        val pickedWord = question.optionsSecond[index]
        val isCorrect = pickedWord.id == question.correctSecondId

        return ClickContext(
            buttonIndex = index,
            question = question,
            picked = pickedWord,
            isCorrect = isCorrect,
            wordsIds = listOf(pickedWord.id, question.correctSecondId)
        )
    }

    private fun canClick(state: GameUiOOF, index: Int): Boolean {
        return !state.locked && index in MIN_BUTTON_INDEX..MAX_BUTTON_INDEX
    }
}
