package com.sleeplessdog.pimi.database.user

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UserAwardDao {

    @Query("SELECT * FROM user_awards")
    suspend fun getAll(): List<UserAwardEntity>

    @Query("SELECT * FROM user_awards WHERE awardId = :id")
    suspend fun getById(id: String): UserAwardEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: UserAwardEntity)

    @Query(
        """
        UPDATE user_awards 
        SET unlocked = 1, unlockedAt = :time 
        WHERE awardId = :id
    """
    )
    suspend fun unlock(id: String, time: Long)
}
