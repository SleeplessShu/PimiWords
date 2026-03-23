package com.sleeplessdog.matchthewords.backend.data.db.user

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface SessionLogDao {
    @Insert
    suspend fun insert(entity: SessionLogEntity)

    @Query("SELECT * FROM session_log WHERE date = :date LIMIT 1")
    suspend fun getByDate(date: String): SessionLogEntity?

    @Query("SELECT * FROM session_log ORDER BY date DESC LIMIT 7")
    suspend fun getLast7(): List<SessionLogEntity>
}