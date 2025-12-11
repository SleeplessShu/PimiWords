package com.sleeplessdog.matchthewords.game.presentation.controller


import com.sleeplessdog.matchthewords.game.domain.api.ScoreInteractor
import com.sleeplessdog.matchthewords.game.domain.interactors.WordsController
import com.sleeplessdog.matchthewords.game.presentation.models.GameSettings
import com.sleeplessdog.matchthewords.game.presentation.models.GameType
import com.sleeplessdog.matchthewords.game.presentation.models.StatsState
import com.sleeplessdog.matchthewords.game.presentation.parentControllers.ProgressController
import com.sleeplessdog.matchthewords.utils.ConstantsApp.MAX_LIVES
import com.sleeplessdog.matchthewords.utils.ConstantsApp.START_LIVES
import com.sleeplessdog.matchthewords.utils.ConstantsApp.ZERO_STRING
import com.sleeplessdog.matchthewords.utils.ConstantsGamePrices
import com.sleeplessdog.matchthewords.utils.SupportFunctions

class GameSessionController(
    private val wordsController: WordsController,
    private val scoreInteractor: ScoreInteractor,
    progressController: ProgressController
) {
    private val economy = GameEconomyController(
        maxLives = MAX_LIVES,
        startLives = START_LIVES
    )

    private val progressManager = GameProgressManager(progressController)

    fun setup(settings: GameSettings, gameType: GameType) {
        val diffValue = SupportFunctions.getGameDifficult(settings.difficult)

        economy.setup(
            difficultLevel = diffValue,
            lives = SupportFunctions.getLivesCount(settings.difficult)
        )

        progressManager.setup(
            difficult = settings.difficult,
            gameType = gameType
        )
    }

    fun onCorrect(ids: List<Int>) {
        economy.addCorrectIds(ids)
        economy.onCorrect(ConstantsGamePrices.ANSWER_PRICE)
        progressManager.onCorrect()
    }

    fun onWrong(ids: List<Int>, gameType: GameType): Boolean {
        economy.addWrongIds(ids)
        val isDead = economy.onWrong(ConstantsGamePrices.MISTAKE_PRICE)
        progressManager.onWrong(gameType)
        return isDead
    }

    suspend fun saveResults(): Int {
        val stats = economy.buildSessionStats()
        wordsController.putRoundStats(stats)
        scoreInteractor.updateTodaysResult(economy.score)
        return scoreInteractor.getTodaysResult()
    }

    suspend fun getTodayScore(): Int {
        return scoreInteractor.getTodaysResult()
    }

    // Сброс для рестарта
    fun resetForRestart() {
        economy.resetScoreOnly()
        progressManager.resetOnlyStep()
    }

    // Генерация стейта для UI
    fun getStatsState(gameType: GameType, todaysScore: String = ZERO_STRING): StatsState {
        return StatsState(
            lives = economy.lives,
            score = economy.score.toString(),
            todaysScore = todaysScore,
            progressSegments = progressManager.progressSegments,
            progress = progressManager.progress(gameType)
        )
    }
}
