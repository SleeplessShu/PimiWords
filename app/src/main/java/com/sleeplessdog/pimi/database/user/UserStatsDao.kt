package com.sleeplessdog.pimi.database.user

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UserStatsDao {
    @Query("SELECT * FROM user_stats WHERE id = 1")
    suspend fun get(): UserStatsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(entity: UserStatsEntity)

    @Query("UPDATE user_stats SET totalGamesPlayed = totalGamesPlayed + 1, weekGamesPlayed = weekGamesPlayed + 1 WHERE id = 1")
    suspend fun incrementGames()

    @Query("UPDATE user_stats SET totalScores = totalScores + :score, weekScores = weekScores + :score WHERE id = 1")
    suspend fun addScore(score: Int)

    @Query("UPDATE user_stats SET totalWordsLearned = totalWordsLearned + :count, weekWordsLearned = weekWordsLearned + :count WHERE id = 1")
    suspend fun addLearnedWords(count: Int)
}