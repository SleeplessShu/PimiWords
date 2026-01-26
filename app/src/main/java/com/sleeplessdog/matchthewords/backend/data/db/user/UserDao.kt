package com.sleeplessdog.matchthewords.backend.data.db.user

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sleeplessdog.matchthewords.backend.domain.models.AppStatisticsEntity
import com.sleeplessdog.matchthewords.backend.domain.models.DailyStatsEntity
import com.sleeplessdog.matchthewords.backend.domain.models.UserSettingsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    // ---------- Groups ----------

    @Query("SELECT * FROM UserGroups")
    fun observeGroups(): Flow<List<UserGroupEntity>>

    @Query("SELECT * FROM UserGroups WHERE groupKey = :key LIMIT 1")
    suspend fun getGroupByKey(key: String): UserGroupEntity?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertGroup(group: UserGroupEntity): Long

    @Delete
    suspend fun deleteGroup(group: UserGroupEntity)

    @Query("SELECT * FROM UserGroups")
    suspend fun getAllGroupsOnce(): List<UserGroupEntity>

    @Query("SELECT selectedGroups FROM UserSettings WHERE id = 1")
    suspend fun getSelectedGroups(): String?

    // ---------- Words ----------

    @Query("SELECT * FROM UserWords")
    suspend fun getAllWords(): List<UserWordEntity>

    @Query("SELECT * FROM UserWords WHERE groupId IN (:groupIds)")
    suspend fun getWordsByGroups(groupIds: Set<Long>): List<UserWordEntity>

    @Query("SELECT * FROM UserWords WHERE globalId = :globalId LIMIT 1")
    suspend fun findByGlobalId(globalId: Long): UserWordEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWord(word: UserWordEntity)

    @Delete
    suspend fun deleteWord(word: UserWordEntity)

    // ---------- Progress ----------

    @Query("SELECT * FROM UserWordProgress WHERE globalId = :globalId")
    suspend fun getProgress(globalId: Long): UserWordProgressEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveProgress(progress: UserWordProgressEntity)

    @Query("SELECT * FROM DailyStats WHERE date = :date")
    suspend fun getDailyStats(date: String): DailyStatsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveDailyStats(stats: DailyStatsEntity)

    @Query("SELECT value FROM AppStatistics WHERE `key` = :statKey")
    suspend fun getStat(statKey: String): String?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setStat(stat: AppStatisticsEntity)

    // ---------- Settings ----------
    @Query("SELECT * FROM UserSettings WHERE id = 1")
    fun observeSettings(): Flow<UserSettingsEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSettings(settings: UserSettingsEntity)
}
