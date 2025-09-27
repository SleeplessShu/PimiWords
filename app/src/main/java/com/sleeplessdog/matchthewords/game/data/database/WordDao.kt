package com.sleeplessdog.matchthewords.game.data.database


import androidx.room.Dao
import androidx.room.Query
import androidx.room.Update
import com.sleeplessdog.matchthewords.game.data.WordEntity

@Dao
interface WordDao {

    // 1) Конкретная категория И конкретный уровень (без лимита), в случайном порядке
    @Query("""
        SELECT * FROM words
        WHERE category = :category COLLATE NOCASE
          AND level    = :level    COLLATE NOCASE
        ORDER BY RANDOM()
    """)
    suspend fun getByCategoryAndLevel(category: String, level: String): List<WordEntity>

    // 2) Конкретная категория И ЛЮБОЙ уровень (все уровни), в случайном порядке
    @Query("""
        SELECT * FROM words
        WHERE category = :category COLLATE NOCASE
        ORDER BY RANDOM()
    """)
    suspend fun getByCategoryAllLevels(category: String): List<WordEntity>

    // 3) ЛЮБАЯ категория И конкретный уровень (все категории), в случайном порядке
    @Query("""
        SELECT * FROM words
        WHERE level = :level COLLATE NOCASE
        ORDER BY RANDOM()
    """)
    suspend fun getAllCategoriesByLevel(level: String): List<WordEntity>

    // 4) ЛЮБАЯ категория И ЛЮБОЙ уровень — конкретное количество, в случайном порядке
    @Query("""
        SELECT * FROM words
        ORDER BY RANDOM()
        LIMIT :limit
    """)
    suspend fun getAny(limit: Int): List<WordEntity>

    // Дополнительно: рандом из любых (если где-то уже используется)
    @Query("SELECT * FROM words ORDER BY RANDOM() LIMIT :wordsNeeded")
    suspend fun getRandom(wordsNeeded: Int): List<WordEntity>

    @Update
    suspend fun updateWord(wordEntity: WordEntity)
}