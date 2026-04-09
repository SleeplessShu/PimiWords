package com.sleeplessdog.pimi.score.domain

import android.util.Log
import com.sleeplessdog.pimi.database.global.GlobalDao
import com.sleeplessdog.pimi.database.user.AwardProgressEntity
import com.sleeplessdog.pimi.database.user.SessionLogEntity
import com.sleeplessdog.pimi.database.user.UserStatsEntity
import com.sleeplessdog.pimi.database.user.WordProgressEntity
import com.sleeplessdog.pimi.score.StatsRepository
import com.sleeplessdog.pimi.score.domain.models.AwardId
import com.sleeplessdog.pimi.utils.GamePrices.LEARNED_THRESHOLD
import java.time.LocalDate

class AwardEngine(
    private val statsRepository: StatsRepository,
    private val globalDao: GlobalDao,
) {
    data class GameResult(
        val correctIds: List<Int>,
        val wrongIds: List<Int>,
        val durationMinutes: Int,
        val groupKey: String?,
        val score: Int,
        val isUserGroup: Boolean = false,
    )

    suspend fun processGameResult(result: GameResult) {
        try {
            val newlyLearnedCount = updateWordProgress(result)
            updateStats(result, newlyLearnedCount)
            updateStreak(result.durationMinutes)
            checkAllAwards(result)
        } catch (e: Exception) {
            Log.e("AwardEngine", "processGameResult failed: $e")
        }
    }

    private suspend fun updateWordProgress(result: GameResult): Int {
        val now = System.currentTimeMillis()
        var newlyLearned = 0

        result.wrongIds.forEach { id ->
            val current = statsRepository.getWordProgress(id)
                ?: WordProgressEntity(wordId = id, groupKey = result.groupKey ?: "")
            statsRepository.saveWordProgress(
                current.copy(
                    wrongCount = current.wrongCount + 1,
                    lastAnsweredAt = now
                )
            )
        }

        result.correctIds.forEach { id ->
            val current = statsRepository.getWordProgress(id)
                ?: WordProgressEntity(wordId = id, groupKey = result.groupKey ?: "")
            val newCorrect = current.correctCount + 1
            val isNowLearned = newCorrect >= LEARNED_THRESHOLD && !current.isLearned

            if (isNowLearned) newlyLearned++

            statsRepository.saveWordProgress(
                current.copy(
                    correctCount = newCorrect,
                    lastAnsweredAt = now,
                    isLearned = newCorrect >= LEARNED_THRESHOLD
                )
            )
        }

        return newlyLearned
    }

    private suspend fun updateStats(result: GameResult, newlyLearnedCount: Int) {
        statsRepository.incrementGames()
        statsRepository.addScore(result.score)
        if (newlyLearnedCount > 0) {
            statsRepository.addLearnedWords(newlyLearnedCount)
        }
    }

    private suspend fun updateStreak(durationMinutes: Int) {
        val stats = statsRepository.getStats() ?: UserStatsEntity()
        val today = LocalDate.now().toString()
        val yesterday = LocalDate.now().minusDays(1).toString()

        val newStreak = when (stats.lastPlayedDate) {
            today -> stats.currentStreak
            yesterday -> stats.currentStreak + 1
            else -> 1
        }

        statsRepository.saveStats(
            stats.copy(
                currentStreak = newStreak,
                lastPlayedDate = today,
                totalSessionMinutes = stats.totalSessionMinutes + durationMinutes
            )
        )

        statsRepository.insertSession(
            SessionLogEntity(
                date = today,
                durationMinutes = durationMinutes,
                wordsCount = 0,
                isCompleted = true
            )
        )
    }

    private suspend fun checkAllAwards(result: GameResult) {
        val stats = statsRepository.getStats() ?: return

        checkWordByWord(stats)
        checkILiveHere(stats)
        checkLittleButRegular()
        checkLazyPanda(result)
        checkHonestly(result)
        checkIUnderstood(result)
        checkSelfImprovement(result)
        checkPerfectAndAlmost(result)
        checkAlmostExpert()
        checkNowForSure(result)
    }

    private suspend fun checkWordByWord(stats: UserStatsEntity) {
        if (stats.totalWordsLearned >= 100) unlock(AwardId.WORD_BY_WORD)
    }

    private suspend fun checkILiveHere(stats: UserStatsEntity) {
        if (stats.currentStreak >= 7) unlock(AwardId.I_LIVE_HERE)
    }

    private suspend fun checkLittleButRegular() {
        val last3 = statsRepository.getLast7Sessions().take(3)
        if (last3.size >= 3 && last3.all { it.durationMinutes in 1..4 }) {
            unlock(AwardId.LITTLE_BUT_REGULAR)
        }
    }

    private suspend fun checkLazyPanda(result: GameResult) {
        if (result.correctIds.size <= 3 && result.correctIds.isNotEmpty()) {
            unlock(AwardId.LAZY_PANDA)
        }
    }

    private suspend fun checkHonestly(result: GameResult) {
        result.correctIds.forEach { id ->
            val progress = statsRepository.getWordProgress(id) ?: return@forEach
            if (progress.wrongCount == 1 && progress.correctCount == 1) {
                unlock(AwardId.HONESTLY)
                return
            }
        }
    }

    private suspend fun checkIUnderstood(result: GameResult) {
        result.correctIds.forEach { id ->
            val progress = statsRepository.getWordProgress(id) ?: return@forEach
            if (progress.wrongCount >= 3 && progress.correctCount >= 1) {
                unlock(AwardId.I_UNDERSTOOD)
                return
            }
        }
    }

    private suspend fun checkSelfImprovement(result: GameResult) {
        val fixed = result.correctIds.count { id ->
            val p = statsRepository.getWordProgress(id)
            p != null && p.wrongCount > 0 && p.correctCount >= LEARNED_THRESHOLD
        }
        if (fixed > 0) incrementProgress(AwardId.SELF_IMPROVEMENT, fixed, target = 10)
    }

    private suspend fun checkPerfectAndAlmost(result: GameResult) {
        val groupKey = result.groupKey ?: return
        if (result.isUserGroup) return

        val totalInGroup = globalDao.countWordsByGroup(groupKey)
        if (totalInGroup == 0) return

        val learnedInGroup = statsRepository.countLearnedInGroup(groupKey)
        val percent = learnedInGroup.toFloat() / totalInGroup

        when {
            percent >= 1.0f -> unlock(AwardId.PERFECTIONIST)
            percent >= 0.99f -> unlock(AwardId.ALMOST)
        }
    }

    private suspend fun checkNowForSure(result: GameResult) {
        val groupKey = result.groupKey ?: return
        if (result.isUserGroup) return

        val totalInGroup = globalDao.countWordsByGroup(groupKey)
        if (totalInGroup == 0) return

        val learnedInGroup = statsRepository.countLearnedInGroup(groupKey)
        val percent = learnedInGroup.toFloat() / totalInGroup

        if (percent >= 1.0f) {
            val previousLearned = learnedInGroup - result.correctIds.size
            val previousPercent = previousLearned.toFloat() / totalInGroup
            if (previousPercent in 0.95f..0.99f) {
                unlock(AwardId.NOW_FOR_SURE)
            }
        }
    }

    private suspend fun checkAlmostExpert() {
        val allGroups = globalDao.getAllGroupKeys()
        var count = 0

        allGroups.forEach { key ->
            val total = globalDao.countWordsByGroup(key)
            val learned = statsRepository.countLearnedInGroup(key)
            if (total > 0 && learned.toFloat() / total >= 0.9f) count++
        }

        if (count >= 5) unlock(AwardId.ALMOST_EXPERT)
    }

    private suspend fun unlock(id: AwardId) {
        if (statsRepository.isAwardUnlocked(id.name)) return
        statsRepository.unlockAward(id.name)
    }

    private suspend fun incrementProgress(id: AwardId, delta: Int, target: Int) {
        val current = statsRepository.getAwardProgress(id.name)
        val newProgress = (current?.progress ?: 0) + delta

        statsRepository.saveAwardProgress(
            AwardProgressEntity(
                awardId = id.name,
                progress = newProgress,
                target = target
            )
        )

        if (newProgress >= target) unlock(id)
    }
}