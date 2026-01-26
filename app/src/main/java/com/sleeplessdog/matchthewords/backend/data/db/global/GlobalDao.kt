package com.sleeplessdog.matchthewords.backend.data.db.global

import androidx.room.Dao
import androidx.room.Query
import com.sleeplessdog.matchthewords.backend.domain.models.LanguageLevel

@Dao
interface GlobalDao {

    @Query(
        """
        SELECT * FROM GlobalDictionary
        WHERE isDeleted = 0
        AND difficulty IN (:levels)
        AND (:groupKeys IS NULL OR groupKey IN (:groupKeys))
    """
    )
    suspend fun getWords(
        levels: Set<LanguageLevel>,
        groupKeys: Set<String>?,
    ): List<GlobalDictionaryEntity>

    @Query("SELECT * FROM GlobalDictionary WHERE id = :id")
    suspend fun getById(id: Long): GlobalDictionaryEntity?

    @Query(
        """
    SELECT DISTINCT groupKey
    FROM GlobalDictionary
    WHERE isDeleted = 0
"""
    )
    suspend fun getAllGroupKeys(): List<String>
}
