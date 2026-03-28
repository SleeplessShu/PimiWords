package com.sleeplessdog.pimi.database.user

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sleeplessdog.pimi.settings.UserSettingsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    // ---------- Groups ----------
    @Query(
        """
    DELETE FROM UserGroups
    WHERE groupKey = :groupKey
"""
    )
    suspend fun deleteGroupByKey(groupKey: String)

    @Query(
        """
    UPDATE UserGroups
    SET title = :newTitle
    WHERE groupKey = :groupKey
"""
    )
    suspend fun updateGroupTitle(
        groupKey: String,
        newTitle: String,
    )

    @Query("SELECT * FROM UserGroups")
    fun observeUserGroups(): Flow<List<UserGroupEntity>>

    @Query("SELECT * FROM UserGroups WHERE groupKey = :key LIMIT 1")
    suspend fun getGroupByKey(key: String): UserGroupEntity?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertGroup(group: UserGroupEntity)

    @Delete
    suspend fun deleteGroup(group: UserGroupEntity)

    @Query("SELECT * FROM UserGroups")
    suspend fun getAllGroupsOnce(): List<UserGroupEntity>

    @Query("SELECT selectedGroups FROM UserSettings WHERE id = 1")
    suspend fun getSelectedGroups(): String?

    @Query("SELECT selectedGroups FROM UserSettings WHERE id = 1")
    fun observeSelectedGroups(): Flow<String?>

    @Query(
        """
    SELECT COUNT(*) 
    FROM UserWords uw
    INNER JOIN UserGroups ug ON uw.groupId = ug.groupKey
    WHERE ug.groupKey = :groupKey
"""
    )
    suspend fun countWordsByGroupKey(groupKey: String): Int

    @Query(
        """
        SELECT title 
        FROM UserGroups
        WHERE groupKey = :groupId
        LIMIT 1
    """
    )
    suspend fun getGroupTitleById(groupId: String): String?

    // ---------- Words ----------

    @Query("SELECT * FROM UserWords WHERE groupId = :groupKey")
    suspend fun getWordsByGroupKey(groupKey: String): List<UserWordEntity>

    @Query("SELECT * FROM UserWords")
    suspend fun getAllWords(): List<UserWordEntity>

    @Query("SELECT * FROM UserWords WHERE groupId IN (:groupIds)")
    suspend fun getWordsByGroups(groupIds: Set<Long>): List<UserWordEntity>

    /**
     * подписка на содержимое юзергруппы
     */
    @Query(
        """
        SELECT * 
        FROM UserWords
        WHERE groupId = :groupId
        ORDER BY addedAt DESC
    """
    )
    fun observeWordsInUserGroup(groupId: String): Flow<List<UserWordEntity>>

    @Query("SELECT * FROM UserWords WHERE globalId = :globalId LIMIT 1")
    suspend fun findByGlobalId(globalId: Long): UserWordEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWord(word: UserWordEntity)

    @Query(
        """
UPDATE UserWords SET
    english  = CASE WHEN :english  IS NOT NULL THEN :english  ELSE english END,
    spanish  = CASE WHEN :spanish  IS NOT NULL THEN :spanish  ELSE spanish END,
    russian  = CASE WHEN :russian  IS NOT NULL THEN :russian  ELSE russian END,
    french   = CASE WHEN :french   IS NOT NULL THEN :french   ELSE french END,
    german   = CASE WHEN :german   IS NOT NULL THEN :german   ELSE german END,
    armenian = CASE WHEN :armenian IS NOT NULL THEN :armenian ELSE armenian END,
    serbian  = CASE WHEN :serbian  IS NOT NULL THEN :serbian  ELSE serbian END
WHERE id = :wordId
  AND groupId = :groupId
  
"""
    )
    suspend fun updateUserWordFields(
        wordId: Long,
        groupId: String,
        english: String?,
        spanish: String?,
        russian: String?,
        french: String?,
        german: String?,
        armenian: String?,
        serbian: String?,
    )

    @Delete
    suspend fun deleteWord(word: UserWordEntity)

    @Query(
        """
DELETE FROM UserWords
WHERE groupId = :groupId
AND id = :wordId
"""
    )
    suspend fun deleteWordByGroupIdAndWordId(
        groupId: String,
        wordId: Long,
    )

    @Query(
        """
UPDATE UserWords
SET groupId = :targetGroupId
WHERE id = :wordId
"""
    )
    suspend fun moveWordToGroup(
        wordId: Long,
        targetGroupId: String,
    )

    // ---------- Settings ----------
    @Query("SELECT * FROM UserSettings WHERE id = 1")
    fun observeSettings(): Flow<UserSettingsEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSettings(settings: UserSettingsEntity)


}
