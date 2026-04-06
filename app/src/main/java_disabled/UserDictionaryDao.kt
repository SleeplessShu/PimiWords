package com.sleeplessdog.pimi.game.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.sleeplessdog.pimi.game.data.UserWordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDictionaryDao {

    @Query("SELECT * FROM user_dictionary ORDER BY dateAdded DESC")
    fun getAllUserWords(): Flow<List<UserWordEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertOrUpdateWord(words: List<UserWordEntity>)

    @Update
    suspend fun updateWord(word: UserWordEntity)

    @Delete
    suspend fun deleteWord(word: UserWordEntity)

    @Query("SELECT * FROM user_dictionary WHERE english = :englishWord LIMIT 1")
    suspend fun findWordByEnglish(englishWord: String): UserWordEntity?


    @Query("SELECT * FROM user_dictionary WHERE id = :wordId")
    suspend fun getWordById(wordId: Long): UserWordEntity?

    @Query("SELECT * FROM user_dictionary WHERE category = :categoryName ORDER BY english ASC")
    fun getWordsByCategory(categoryName: String): Flow<List<UserWordEntity>>

    @Query(
        """
        SELECT * FROM user_dictionary
        WHERE category = :categoryName
        ORDER BY RANDOM()
    """
    )
    suspend fun getAllWordsOfCategory(
        categoryName: String,
    ): List<UserWordEntity>

}