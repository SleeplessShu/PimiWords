package com.sleeplessdog.matchthewords.score.presentation

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sleeplessdog.matchthewords.game.domain.api.ScoreInteractor
import com.sleeplessdog.matchthewords.score.models.GameResult
import com.sleeplessdog.matchthewords.utils.SupportFunctions


class ScoreViewModel(
    private val scoreInteractor: ScoreInteractor,
    private val supportFunctions: SupportFunctions,
) : ViewModel() {
    private val _scoreResults = MutableLiveData<List<GameResult>>()
    val scoreResults: LiveData<List<GameResult>> get() = _scoreResults


    init {
        observeScoreChanges()
    }

    private fun observeScoreChanges() {
        scoreInteractor.getAllDaysResults().observeForever { databaseResponse ->
            val updatedScoreList = databaseResponse.map { (date, score) ->
                GameResult(
                    date = date,
                    score = supportFunctions.getScoreAsString(score)
                )
            }
            _scoreResults.postValue(updatedScoreList)
        }
    }
}