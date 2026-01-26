package com.sleeplessdog.matchthewords.game.domain.interactors

import androidx.lifecycle.LiveData
import com.sleeplessdog.matchthewords.game.domain.api.ScoreInteractor
import com.sleeplessdog.matchthewords.game.domain.repositories.ScoreRepository

class ScoreInteractorImpl(
    private val scoreRepository: ScoreRepository
): ScoreInteractor {
    override fun updateTodaysResult(matchResult: Int) {
        scoreRepository.updateTodaysResult(matchResult)
    }

    override fun getTodaysResult(): Int {
        return scoreRepository.getTodaysResult()
    }

    override fun getAllDaysResults(): LiveData<Map<String, Int>> {
        return scoreRepository.getAllDaysResults()
    }
}