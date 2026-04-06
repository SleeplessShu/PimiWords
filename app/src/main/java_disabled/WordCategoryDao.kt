package com.sleeplessdog.pimi.game.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.sleeplessdog.pimi.game.data.WordCategoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WordCategoryDao {

    // Сначала пользовательские, затем дефолтные; внутри — по order_in_block и titleKey
    @Query(
        """
        SELECT * FROM word_categories
        ORDER BY is_user DESC, order_in_block ASC, title_key ASC
    """
    )
    fun observeAll(): Flow<List<WordCategoryEntity>>

    // Избранные для главного экрана (например, первые 8)
    @Query(
        """
        SELECT * FROM word_categories
        ORDER BY 
            CASE WHEN is_selected = 1 THEN 0 ELSE 1 END,
            is_user DESC,
            order_in_block ASC,
            title_key ASC
        LIMIT :limit
    """
    )
    fun observeFeatured(limit: Int): Flow<List<WordCategoryEntity>>

    // Тоггл выбора
    @Query("UPDATE word_categories SET is_selected = :selected WHERE `key` = :key")
    suspend fun setSelected(key: String, selected: Boolean)

    // Массовое сохранение выбора (для Save из bottom sheet)
    @Transaction
    suspend fun replaceSelection(selectedKeys: Set<String>) {
        clearSelection()
        if (selectedKeys.isNotEmpty()) setSelectionBulk(selectedKeys.toList())
    }

    @Query("UPDATE word_categories SET is_selected = 0")
    suspend fun clearSelection()

    @Query("UPDATE word_categories SET is_selected = 1 WHERE `key` IN (:keys)")
    suspend fun setSelectionBulk(keys: List<String>)

    // Пользовательские категории
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(category: WordCategoryEntity)

    @Query("DELETE FROM word_categories WHERE `key` = :key AND is_user = 1")
    suspend fun deleteUserCategory(key: String)

    @Query("SELECT * FROM word_categories WHERE `key` = :key LIMIT 1")
    suspend fun getByKey(key: String): WordCategoryEntity?
}