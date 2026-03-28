package com.sleeplessdog.pimi.database.user

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface WordProgressDao {
    @Query("SELECT * FROM word_progress WHERE wordId = :id")
    suspend fun get(id: Int): WordProgressEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(entity: WordProgressEntity)

    @Query("SELECT COUNT(*) FROM word_progress WHERE isLearned = 1")
    suspend fun countLearned(): Int

    @Query("SELECT COUNT(*) FROM word_progress WHERE isLearned = 1 AND groupKey = :groupKey")
    suspend fun countLearnedInGroup(groupKey: String): Int

    @Query("SELECT wordId FROM word_progress WHERE isLearned = 1")
    suspend fun getLearnedWordIds(): List<Int>

    @Query("UPDATE word_progress SET correctCount = correctCount + 1, lastAnsweredAt = :time WHERE wordId = :id")
    suspend fun incrementCorrect(id: Int, time: Long)

    @Query("UPDATE word_progress SET wrongCount = wrongCount + 1 WHERE wordId = :id")
    suspend fun incrementWrong(id: Int)

    @Query("UPDATE word_progress SET isLearned = 1 WHERE wordId = :id")
    suspend fun markLearned(id: Int)
}