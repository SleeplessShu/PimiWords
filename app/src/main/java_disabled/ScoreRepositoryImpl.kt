package com.sleeplessdog.pimi.game.data.repositories

import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sleeplessdog.pimi.game.domain.repositories.ScoreRepository
import com.sleeplessdog.pimi.utils.SupportFunctions

class ScoreRepositoryImpl(
    private var sharedPreferences: SharedPreferences,
) : ScoreRepository {

    private val _allDaysResults = MutableLiveData<Map<String, Int>>()

    init {
        _allDaysResults.value =
            SupportFunctions.sortMapByDateDescending(sharedPreferences.all.filterValues { it is Int }
                .mapValues { it.value as Int })

    }

    override fun updateTodaysResult(matchResult: Int) {
        val currentDate = SupportFunctions.getCurrentDate()
        val currentResult = getTodaysResult()
        val newResult = currentResult + matchResult

        sharedPreferences.edit { putInt(currentDate, newResult) }

        val updatedMap =
            sharedPreferences.all.filterValues { it is Int }.mapValues { it.value as Int }
        _allDaysResults.postValue(SupportFunctions.sortMapByDateDescending(updatedMap))
    }

    override fun getTodaysResult(): Int {
        val currentDate = SupportFunctions.getCurrentDate()
        return sharedPreferences.getInt(currentDate, 0)
    }

    override fun getAllDaysResults(): LiveData<Map<String, Int>> = _allDaysResults
}