package com.sleeplessdog.pimi.score

import com.sleeplessdog.pimi.database.AppDatabaseProvider
import com.sleeplessdog.pimi.database.user.AwardProgressEntity
import com.sleeplessdog.pimi.database.user.SessionLogEntity
import com.sleeplessdog.pimi.database.user.UserAwardEntity
import com.sleeplessdog.pimi.database.user.UserDatabase
import com.sleeplessdog.pimi.database.user.UserStatsEntity
import com.sleeplessdog.pimi.database.user.WordProgressEntity
import com.sleeplessdog.pimi.games.data.repository.AppPrefs

class StatsRepository(
    private val databaseProvider: AppDatabaseProvider,
    private val appPrefs: AppPrefs,
) {
    private suspend fun userStatsDao() = getUserDbSafe().userStatsDao()

    private suspend fun wordProgressDao() = getUserDbSafe().wordProgressDao()

    private suspend fun sessionLogDao() = getUserDbSafe().sessionLogDao()

    private suspend fun userAwardDao() = getUserDbSafe().userAwardDao()

    private suspend fun awardProgressDao() = getUserDbSafe().awardProgressDao()

    suspend fun getStats(): UserStatsEntity? = userStatsDao().get()

    suspend fun saveStats(entity: UserStatsEntity) {
        userStatsDao().save(entity)
        appPrefs.markLocalDatabaseDirty()
    }

    suspend fun incrementGames() {
        userStatsDao().incrementGames()
        appPrefs.markLocalDatabaseDirty()
    }

    suspend fun addScore(score: Int) {
        userStatsDao().addScore(score)
        appPrefs.markLocalDatabaseDirty()
    }

    suspend fun addLearnedWords(count: Int) {
        userStatsDao().addLearnedWords(count)
        appPrefs.markLocalDatabaseDirty()
    }

    suspend fun getWordProgress(id: Int): WordProgressEntity? = wordProgressDao().get(id)

    suspend fun saveWordProgress(entity: WordProgressEntity) {
        wordProgressDao().save(entity)
        appPrefs.markLocalDatabaseDirty()
    }

    suspend fun countLearnedInGroup(groupKey: String): Int =
        wordProgressDao().countLearnedInGroup(groupKey)

    suspend fun countTotalLearned(): Int = wordProgressDao().countLearned()

    suspend fun insertSession(entity: SessionLogEntity) {
        sessionLogDao().insert(entity)
        appPrefs.markLocalDatabaseDirty()
    }

    suspend fun getLast7Sessions(): List<SessionLogEntity> = sessionLogDao().getLast7()

    suspend fun getUnlockedAwardIds(): Set<String> =
        userAwardDao().getAll().filter { it.unlocked }.map { it.awardId }.toSet()

    suspend fun unlockAward(awardId: String) {
        userAwardDao().insert(
            UserAwardEntity(
                awardId = awardId, unlocked = true, unlockedAt = System.currentTimeMillis()
            )
        )
        appPrefs.markLocalDatabaseDirty()
    }

    suspend fun isAwardUnlocked(awardId: String): Boolean =
        userAwardDao().getById(awardId)?.unlocked == true

    suspend fun getAwardProgress(awardId: String): AwardProgressEntity? =
        awardProgressDao().get(awardId)

    suspend fun saveAwardProgress(entity: AwardProgressEntity) {
        awardProgressDao().insert(entity)
        appPrefs.markLocalDatabaseDirty()
    }

    private suspend fun getUserDbSafe(): UserDatabase {
        return try {
            databaseProvider.getUserDatabase()
        } catch (e: Exception) {
            kotlinx.coroutines.delay(200)
            try {
                databaseProvider.getUserDatabase()
            } catch (e2: Exception) {
                databaseProvider.openUserDatabase()
                databaseProvider.getUserDatabase()
            }
        }
    }


}