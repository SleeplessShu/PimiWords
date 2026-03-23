package com.sleeplessdog.matchthewords.backend.data.repository

import com.sleeplessdog.matchthewords.backend.data.db.AppDatabaseProvider
import com.sleeplessdog.matchthewords.backend.data.db.user.AwardProgressEntity
import com.sleeplessdog.matchthewords.backend.data.db.user.SessionLogEntity
import com.sleeplessdog.matchthewords.backend.data.db.user.UserAwardEntity
import com.sleeplessdog.matchthewords.backend.data.db.user.UserStatsEntity
import com.sleeplessdog.matchthewords.backend.data.db.user.WordProgressEntity

class StatsRepository(
    private val databaseProvider: AppDatabaseProvider,
) {
    private suspend fun userStatsDao() =
        databaseProvider.getUserDatabase().userStatsDao()

    private suspend fun wordProgressDao() =
        databaseProvider.getUserDatabase().wordProgressDao()

    private suspend fun sessionLogDao() =
        databaseProvider.getUserDatabase().sessionLogDao()

    private suspend fun userAwardDao() =
        databaseProvider.getUserDatabase().userAwardDao()

    private suspend fun awardProgressDao() =
        databaseProvider.getUserDatabase().awardProgressDao()

    suspend fun getStats(): UserStatsEntity? =
        userStatsDao().get()

    suspend fun saveStats(entity: UserStatsEntity) =
        userStatsDao().save(entity)

    suspend fun incrementGames() =
        userStatsDao().incrementGames()

    suspend fun addScore(score: Int) =
        userStatsDao().addScore(score)

    suspend fun addLearnedWords(count: Int) =
        userStatsDao().addLearnedWords(count)

    suspend fun getWordProgress(id: Int): WordProgressEntity? =
        wordProgressDao().get(id)

    suspend fun saveWordProgress(entity: WordProgressEntity) =
        wordProgressDao().save(entity)

    suspend fun countLearnedInGroup(groupKey: String): Int =
        wordProgressDao().countLearnedInGroup(groupKey)

    suspend fun countTotalLearned(): Int =
        wordProgressDao().countLearned()

    suspend fun insertSession(entity: SessionLogEntity) =
        sessionLogDao().insert(entity)

    suspend fun getLast7Sessions(): List<SessionLogEntity> =
        sessionLogDao().getLast7()

    suspend fun getUnlockedAwardIds(): Set<String> =
        userAwardDao().getAll()
            .filter { it.unlocked }
            .map { it.awardId }
            .toSet()

    suspend fun unlockAward(awardId: String) {
        userAwardDao().insert(
            UserAwardEntity(
                awardId = awardId,
                unlocked = true,
                unlockedAt = System.currentTimeMillis()
            )
        )
    }

    suspend fun isAwardUnlocked(awardId: String): Boolean =
        userAwardDao().getById(awardId)?.unlocked == true

    suspend fun getAwardProgress(awardId: String): AwardProgressEntity? =
        awardProgressDao().get(awardId)

    suspend fun saveAwardProgress(entity: AwardProgressEntity) =
        awardProgressDao().insert(entity)
}