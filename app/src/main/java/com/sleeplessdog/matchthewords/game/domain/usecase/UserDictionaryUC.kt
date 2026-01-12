package com.sleeplessdog.matchthewords.game.domain.usecase

import android.util.Log
import androidx.sqlite.db.SimpleSQLiteQuery
import com.sleeplessdog.matchthewords.game.data.database.AppDictionaryDatabase
import com.sleeplessdog.matchthewords.game.data.repositories.UserDictionaryRepository

class AddWordToUserDictionaryUC(
    private val appDictionaryDatabase: AppDictionaryDatabase,
    private val userRepository: UserDictionaryRepository,
) {
    suspend fun execute(wordsIds: List<Int>): Result<Unit> {
        return try {
            Log.d("AddWordToUserDictionaryUC", "wordsIds: $wordsIds")
            val wordsToSave = appDictionaryDatabase.wordDao().getByIds(wordsIds)
            Log.d("AddWordToUserDictionaryUC", "wordstosave: $wordsToSave")
            if (wordsToSave.isEmpty()) {
                return Result.failure(Exception("Слова для сохранения не найдены в основной базе данных."))
            }


            userRepository.addWordFromMainDb(wordsToSave)
            Log.d("USERDATABASE", "userRepository.addWordFromMainDb: $wordsToSave")
            Result.success(Unit)

        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    suspend operator fun invoke(wordsIds: List<Int>) = execute(wordsIds)

    fun checkpoint(db: AppDictionaryDatabase) {
        val query = SimpleSQLiteQuery("PRAGMA wal_checkpoint(FULL)")
        db.query(query)
    }
}