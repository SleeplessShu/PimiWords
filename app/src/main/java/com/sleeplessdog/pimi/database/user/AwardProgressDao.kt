package com.sleeplessdog.pimi.database.user

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface AwardProgressDao {

    @Query("SELECT * FROM award_progress WHERE awardId = :id")
    suspend fun get(id: String): AwardProgressEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: AwardProgressEntity)

    @Query(
        """
        UPDATE award_progress 
        SET progress = progress + :delta
        WHERE awardId = :id
    """
    )
    suspend fun increment(id: String, delta: Int)
}
