package com.sleeplessdog.matchthewords.game.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.sleeplessdog.matchthewords.game.data.UserWordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDictionaryDao {

    /**
     * Получить все слова из словаря пользователя в виде потока данных (Flow).
     * UI будет автоматически обновляться при изменениях.
     */
    @Query("SELECT * FROM user_dictionary ORDER BY dateAdded DESC")
    fun getAllUserWords(): Flow<List<UserWordEntity>>

    /**
     * Вставить новое слово. OnConflictStrategy.IGNORE означает,
     * что если мы попытаемся вставить слово с уже существующим id, операция будет проигнорирована.
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertWord(word: UserWordEntity)

    /**
     * Обновить существующее слово (например, изменить заметки или перевод).
     */
    @Update
    suspend fun updateWord(word: UserWordEntity)

    /**
     * Удалить слово из словаря.
     */
    @Delete
    suspend fun deleteWord(word: UserWordEntity)

    /**
     * Найти слово по его оригинальному написанию и языкам (для проверок).
     */
    @Query("SELECT * FROM user_dictionary WHERE originalWord = :original AND sourceLang = :sourceLang AND targetLang = :targetLang LIMIT 1")
    suspend fun findWord(original: String, sourceLang: String, targetLang: String): UserWordEntity?
}