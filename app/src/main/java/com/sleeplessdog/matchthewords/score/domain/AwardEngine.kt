package com.sleeplessdog.matchthewords.score.domain

import com.sleeplessdog.matchthewords.backend.data.db.global.GlobalDao
import com.sleeplessdog.matchthewords.backend.data.db.user.AwardProgressEntity
import com.sleeplessdog.matchthewords.backend.data.db.user.SessionLogEntity
import com.sleeplessdog.matchthewords.backend.data.db.user.UserStatsEntity
import com.sleeplessdog.matchthewords.backend.data.db.user.WordProgressEntity
import com.sleeplessdog.matchthewords.backend.data.repository.StatsRepository
import com.sleeplessdog.matchthewords.score.domain.models.AwardId
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
        val newlyLearnedCount = updateWordProgress(result)
        updateStats(result, newlyLearnedCount)
        updateStreak(result.durationMinutes)
        checkAllAwards(result)
    }

    // возвращает количество слов которые стали "изученными" в этой сессии
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

        // логируем сессию
        statsRepository.insertSession(
            SessionLogEntity(
                date = today,
                durationMinutes = durationMinutes,
                wordsCount = 0, // заполняется снаружи если нужно
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

    // 100 изученных слов
    private suspend fun checkWordByWord(stats: UserStatsEntity) {
        if (stats.totalWordsLearned >= 100) unlock(AwardId.WORD_BY_WORD)
    }

    // 7 дней подряд
    private suspend fun checkILiveHere(stats: UserStatsEntity) {
        if (stats.currentStreak >= 7) unlock(AwardId.I_LIVE_HERE)
    }

    // 3 дня подряд < 5 минут
    private suspend fun checkLittleButRegular() {
        val last3 = statsRepository.getLast7Sessions().take(3)
        if (last3.size >= 3 && last3.all { it.durationMinutes in 1..4 }) {
            unlock(AwardId.LITTLE_BUT_REGULAR)
        }
    }

    // урок из 3 слов
    private suspend fun checkLazyPanda(result: GameResult) {
        if (result.correctIds.size <= 3 && result.correctIds.isNotEmpty()) {
            unlock(AwardId.LAZY_PANDA)
        }
    }

    // правильно со второй попытки (1 ошибка, потом правильно)
    private suspend fun checkHonestly(result: GameResult) {
        result.correctIds.forEach { id ->
            val progress = statsRepository.getWordProgress(id) ?: return@forEach
            if (progress.wrongCount == 1 && progress.correctCount == 1) {
                unlock(AwardId.HONESTLY)
                return
            }
        }
    }

    // правильно после 3+ ошибок
    private suspend fun checkIUnderstood(result: GameResult) {
        result.correctIds.forEach { id ->
            val progress = statsRepository.getWordProgress(id) ?: return@forEach
            if (progress.wrongCount >= 3 && progress.correctCount >= 1) {
                unlock(AwardId.I_UNDERSTOOD)
                return
            }
        }
    }

    // исправил 10 ошибочных слов (правильно после ошибок, 3+ раза)
    private suspend fun checkSelfImprovement(result: GameResult) {
        val fixed = result.correctIds.count { id ->
            val p = statsRepository.getWordProgress(id)
            p != null && p.wrongCount > 0 && p.correctCount >= LEARNED_THRESHOLD
        }
        if (fixed > 0) incrementProgress(AwardId.SELF_IMPROVEMENT, fixed, target = 10)
    }

    // PERFECTIONIST (100%) и ALMOST (99%)
    private suspend fun checkPerfectAndAlmost(result: GameResult) {
        val groupKey = result.groupKey ?: return
        if (result.isUserGroup) return // для юзерских групп не считаем

        val totalInGroup = globalDao.countWordsByGroup(groupKey)
        if (totalInGroup == 0) return

        val learnedInGroup = statsRepository.countLearnedInGroup(groupKey)
        val percent = learnedInGroup.toFloat() / totalInGroup

        when {
            percent >= 1.0f -> unlock(AwardId.PERFECTIONIST)
            percent >= 0.99f -> unlock(AwardId.ALMOST)
        }
    }

    // NOW_FOR_SURE — довёл с 95-99% до 100%
    private suspend fun checkNowForSure(result: GameResult) {
        val groupKey = result.groupKey ?: return
        if (result.isUserGroup) return

        val totalInGroup = globalDao.countWordsByGroup(groupKey)
        if (totalInGroup == 0) return

        val learnedInGroup = statsRepository.countLearnedInGroup(groupKey)
        val percent = learnedInGroup.toFloat() / totalInGroup

        // текущий процент 100% — значит только что добрались
        if (percent >= 1.0f) {
            // проверяем что прогресс не захардкожен, это честное достижение
            val previousLearned = learnedInGroup - result.correctIds.size
            val previousPercent = previousLearned.toFloat() / totalInGroup
            if (previousPercent in 0.95f..0.99f) {
                unlock(AwardId.NOW_FOR_SURE)
            }
        }
    }

    // 5 категорий по 90%+
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

    companion object {
        const val LEARNED_THRESHOLD = 3
    }
}