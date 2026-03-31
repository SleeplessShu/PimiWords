package com.sleeplessdog.pimi.score

import com.sleeplessdog.pimi.database.global.GlobalDao
import com.sleeplessdog.pimi.database.user.UserStatsEntity
import com.sleeplessdog.pimi.database.user.WordProgressDao
import com.sleeplessdog.pimi.score.domain.AwardEngine
import com.sleeplessdog.pimi.score.domain.models.AwardsCatalog
import com.sleeplessdog.pimi.score.presentation.models.ScoreUiState
import com.sleeplessdog.pimi.score.presentation.models.StatItem
import com.sleeplessdog.pimi.settings.LanguageLevel
import java.time.DayOfWeek
import java.time.LocalDate


class ProcessGameResultUC(
    private val awardEngine: AwardEngine,
) {
    suspend operator fun invoke(
        score: Int,
        correctIds: List<Int>,
        wrongIds: List<Int>,
        durationMinutes: Int,
        groupKey: String? = null,
        isUserGroup: Boolean = false,
    ) {
        awardEngine.processGameResult(
            AwardEngine.GameResult(
                correctIds = correctIds,
                wrongIds = wrongIds,
                durationMinutes = durationMinutes,
                groupKey = groupKey,
                score = score,
                isUserGroup = isUserGroup,
            )
        )
    }
}

class GetScoreUiStateUC(
    private val statsRepository: StatsRepository,
    private val globalDao: GlobalDao,
    private val wordProgressDao: WordProgressDao,
) {
    suspend operator fun invoke(): ScoreUiState {
        val stats = statsRepository.getStats() ?: UserStatsEntity()
        val unlockedIds = statsRepository.getUnlockedAwardIds()

        val awards = AwardsCatalog.all.map { meta ->
            meta.copy(isLocked = meta.id.name !in unlockedIds)
        }

        val level = calculateLevel()
        val currentStats = refreshWeeklyStatsIfNeeded(stats)
        val learnedCategories = countLearnedCategories()

        return ScoreUiState(
            level = level,
            awards = awards,
            statsWeek = StatItem(
                wordsLearned = currentStats.weekWordsLearned,
                categoriesLearned = learnedCategories,
                gamesPlayed = currentStats.weekGamesPlayed,
                scoresGained = currentStats.weekScores,
            ),
            statsAllTime = StatItem(
                wordsLearned = currentStats.totalWordsLearned,
                categoriesLearned = learnedCategories,
                gamesPlayed = currentStats.totalGamesPlayed,
                scoresGained = currentStats.totalScores,
            )
        )
    }

    private suspend fun calculateLevel(): LanguageLevel {
        val learnedIds = wordProgressDao.getLearnedWordIds().toSet()

        var playerLevel = LanguageLevel.A1

        for (level in LanguageLevel.values()) {
            val total = globalDao.countWordsByLevel(level)
            if (total == 0) continue

            val wordIdsForLevel = globalDao.getWordIdsByLevel(level)

            val learnedCount = wordIdsForLevel.count { it.toInt() in learnedIds }
            val percent = learnedCount.toFloat() / total

            if (percent >= 0.9f) {
                playerLevel = level
            } else {
                break
            }
        }

        return playerLevel
    }


    private suspend fun countLearnedCategories(): Int {
        val allGroups = globalDao.getAllGroupKeys()
        return allGroups.count { key ->
            val total = globalDao.countWordsByGroup(key)
            val learned = statsRepository.countLearnedInGroup(key)
            total > 0 && learned.toFloat() / total >= 0.9f
        }
    }

    private suspend fun refreshWeeklyStatsIfNeeded(
        stats: UserStatsEntity,
    ): UserStatsEntity {
        val weekStart = LocalDate.now()
            .with(DayOfWeek.MONDAY)
            .toEpochDay()

        return if (stats.weekStartTimestamp < weekStart) {
            val reset = stats.copy(
                weekWordsLearned = 0,
                weekGamesPlayed = 0,
                weekScores = 0,
                weekStartTimestamp = weekStart
            )
            statsRepository.saveStats(reset)
            reset
        } else {
            stats
        }
    }
}