package com.sleeplessdog.pimi.database.global

import androidx.room.Dao
import androidx.room.Query
import com.sleeplessdog.pimi.settings.LanguageLevel
import kotlinx.coroutines.flow.Flow

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

    @Query(
        """
SELECT * FROM GlobalDictionary
WHERE isDeleted = 0
AND difficulty IN (:levels)
"""
    )
    suspend fun getWordsWOGroups(
        levels: Set<LanguageLevel>,
    ): List<GlobalDictionaryEntity>


    @Query(
        """
SELECT * FROM GlobalDictionary
WHERE isDeleted = 0
AND difficulty IN (:levels)
AND groupKey IN (:groupKeys)
"""
    )
    suspend fun getWordsWGroups(
        levels: Set<LanguageLevel>,
        groupKeys: Set<String>,
    ): List<GlobalDictionaryEntity>


    @Query(
        """
        SELECT * FROM GlobalDictionary
        WHERE isDeleted = 0
        AND groupKey = :groupKey
    """
    )
    suspend fun getWordsByGroup(
        groupKey: String,
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

    @Query(
        """
    SELECT DISTINCT groupKey
    FROM GlobalDictionary
    WHERE isDeleted = 0
"""
    )
    fun observeAllGroupKeys(): Flow<List<String>>

    @Query(
        """
    SELECT COUNT(*) 
    FROM GlobalDictionary 
    WHERE groupKey = :groupKey AND isDeleted = 0
"""
    )
    suspend fun countWordsByGroup(groupKey: String): Int

    @Query(
        """
        SELECT groupKey 
        FROM GlobalDictionary
        WHERE groupKey = :groupId
        LIMIT 1
    """
    )
    suspend fun getGroupTitleById(groupId: String): String?

    @Query("SELECT COUNT(*) FROM GlobalDictionary WHERE difficulty = :level")
    suspend fun countWordsByLevel(level: LanguageLevel): Int

    @Query("SELECT id FROM GlobalDictionary WHERE difficulty = :level AND isDeleted = 0")
    suspend fun getWordIdsByLevel(level: LanguageLevel): List<Long>
}
