package com.sleeplessdog.matchthewords.game.presentation.parentControllers

import android.util.Log
import com.sleeplessdog.matchthewords.game.presentation.models.DifficultLevel
import com.sleeplessdog.matchthewords.game.presentation.models.GameType
import com.sleeplessdog.matchthewords.utils.SupportFunctions

class ProgressController(private val support: SupportFunctions) {

    fun stepsFor(difficult: DifficultLevel, type: GameType): Int {
        val wordsInMatch = support.getGameDifficult(difficult)
        return when (type) {
            GameType.MATCH8 -> (wordsInMatch / MATCH8_STEP_SIZE).coerceAtLeast(1)
            GameType.TRUEorFALSE -> wordsInMatch
            GameType.OneOfFour -> wordsInMatch
            GameType.WriteTheWord -> wordsInMatch / WTW_STEP_SIZE
        }
    }

    fun progressOf(currentStep: Int, segments: Int, type: GameType): Float {
        if (segments <= 0) return 0f

        return when (type) {
            GameType.MATCH8 -> {
                val screensPassed = currentStep / MATCH8_STEP_SIZE
                val res = (screensPassed.toFloat() / segments).coerceIn(0f, 1f)
                res
            }
            else -> {
                (currentStep.toFloat() / segments).coerceIn(0f, 1f)
            }
        }
    }

    fun advancesOnWrong(type: GameType): Boolean =
        type == GameType.TRUEorFALSE

    private companion object {
        const val MATCH8_STEP_SIZE = 6
        const val WTW_STEP_SIZE = 6
    }
}
