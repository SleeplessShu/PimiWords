package com.sleeplessdog.matchthewords.game.data.database


import androidx.room.Dao
import androidx.room.Query
import androidx.room.Update
import com.sleeplessdog.matchthewords.game.data.WordEntity

@Dao
interface WordDao {

    // 1) Конкретные категории И конкретные уровни (множественные значения), случайный порядок
    @Query(
        """
        SELECT * FROM words
        WHERE category IN (:categories) COLLATE NOCASE
          AND level    IN (:levels)    COLLATE NOCASE
        ORDER BY RANDOM()
    """
    )
    suspend fun getByCategoriesAndLevels(
        categories: List<String>,
        levels: List<String>,
    ): List<WordEntity>

    // 2) Конкретные категории И ЛЮБОЙ уровень (все уровни), случайный порядок
    @Query(
        """
        SELECT * FROM words
        WHERE category IN (:categories) COLLATE NOCASE
        ORDER BY RANDOM()
    """
    )
    suspend fun getByCategoriesAllLevels(
        categories: List<String>,
    ): List<WordEntity>

    // 3) ЛЮБАЯ категория И конкретные уровни (все категории), случайный порядок
    @Query(
        """
        SELECT * FROM words
        WHERE level IN (:levels) COLLATE NOCASE
        ORDER BY RANDOM()
    """
    )
    suspend fun getAllCategoriesByLevels(
        levels: List<String>,
    ): List<WordEntity>

    // 4) ЛЮБАЯ категория И ЛЮБОЙ уровень — конкретное количество, случайный порядок
    @Query(
        """
        SELECT * FROM words
        ORDER BY RANDOM()
        LIMIT :limit
    """
    )
    suspend fun getAny(limit: Int): List<WordEntity>

    @Query("SELECT * FROM words ORDER BY RANDOM() LIMIT :wordsNeeded")
    suspend fun getRandom(wordsNeeded: Int): List<WordEntity>

    @Update
    suspend fun updateWord(wordEntity: WordEntity)

    @Query(
        """
        SELECT * FROM words
        WHERE id IN (:ids)
    """
    )
    suspend fun getByIds(ids: List<Int>): List<WordEntity>
}