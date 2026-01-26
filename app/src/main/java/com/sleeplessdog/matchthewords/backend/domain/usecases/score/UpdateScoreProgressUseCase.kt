package com.sleeplessdog.matchthewords.backend.domain.usecases.score

import com.sleeplessdog.matchthewords.backend.data.db.user.UserDao
import com.sleeplessdog.matchthewords.backend.domain.models.AppStatisticsEntity
import com.sleeplessdog.matchthewords.backend.domain.models.DailyStatsEntity
import com.sleeplessdog.matchthewords.utils.ConstantsPaths.CURRENT_STREAK
import com.sleeplessdog.matchthewords.utils.ConstantsPaths.LAST_PLAY_DATE
import com.sleeplessdog.matchthewords.utils.ConstantsPaths.TOTAL_GAMES
import com.sleeplessdog.matchthewords.utils.ConstantsPaths.TOTAL_POINTS
import java.time.LocalDate

class UpdateScoreProgressUseCase(
    private val userDao: UserDao,
) {

    suspend fun update(
        score: Int,
        correctCount: Int,
        mistakeCount: Int,
        timeSpentMs: Long,
    ) {
        val today = LocalDate.now().toString()

        updateDaily(today, score, correctCount, mistakeCount, timeSpentMs)
        updateGlobal(today, score)
    }

    // ---------- Daily ----------

    private suspend fun updateDaily(
        date: String,
        score: Int,
        correct: Int,
        mistake: Int,
        timeMs: Long,
    ) {
        val current = userDao.getDailyStats(date)

        val updated = if (current == null) {
            DailyStatsEntity(
                date = date,
                points = score,
                gamesPlayed = 1,
                wordsCorrect = correct,
                wordsMistake = mistake,
                timeSpentMs = timeMs
            )
        } else {
            current.copy(
                points = current.points + score,
                gamesPlayed = current.gamesPlayed + 1,
                wordsCorrect = current.wordsCorrect + correct,
                wordsMistake = current.wordsMistake + mistake,
                timeSpentMs = current.timeSpentMs + timeMs
            )
        }

        userDao.saveDailyStats(updated)
    }

    // ---------- Global ----------

    private suspend fun updateGlobal(today: String, score: Int) {

        // total points
        val totalPoints = userDao.getStat(TOTAL_POINTS)?.toLong() ?: 0L
        userDao.setStat(
            AppStatisticsEntity(
                key = TOTAL_POINTS,
                value = (totalPoints + score).toString()
            )
        )

        // streak
        val lastDate = userDao.getStat(LAST_PLAY_DATE)
        val streak = userDao.getStat(CURRENT_STREAK)?.toInt() ?: 0

        val newStreak = when {
            lastDate == null -> 1
            lastDate == today -> streak
            LocalDate.parse(lastDate).plusDays(1).toString() == today -> streak + 1
            else -> 1
        }

        userDao.setStat(
            AppStatisticsEntity(
                key = CURRENT_STREAK,
                value = newStreak.toString()
            )
        )

        userDao.setStat(
            AppStatisticsEntity(
                key = LAST_PLAY_DATE,
                value = today
            )
        )

        // total games
        val totalGames = userDao.getStat(TOTAL_GAMES)?.toInt() ?: 0
        userDao.setStat(
            AppStatisticsEntity(
                key = TOTAL_GAMES,
                value = (totalGames + 1).toString()
            )
        )
    }

}