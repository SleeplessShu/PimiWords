package com.sleeplessdog.pimi.games.presentation.parentControllers

import com.sleeplessdog.pimi.games.presentation.models.GameType
import com.sleeplessdog.pimi.settings.DifficultyLevel
import com.sleeplessdog.pimi.utils.GamePrices.MATCH8_STEP_SIZE
import com.sleeplessdog.pimi.utils.GamePrices.WTW_STEP_SIZE
import com.sleeplessdog.pimi.utils.SupportFunctions

class ProgressController() {

    fun stepsFor(difficult: DifficultyLevel, type: GameType): Int {
        val wordsInMatch = SupportFunctions.getGameDifficult(difficult)
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
}
