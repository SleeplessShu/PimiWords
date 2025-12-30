package com.sleeplessdog.matchthewords.game.domain.usecase

import com.sleeplessdog.matchthewords.game.data.database.AppDatabase
import com.sleeplessdog.matchthewords.game.data.repositories.UserDictionaryRepository
import javax.inject.Inject

class AddWordToUserDictionaryUC @Inject constructor(
    private val appDatabase: AppDatabase,
    private val userRepository: UserDictionaryRepository,
) {
    suspend fun execute(wordsIds: Set<Int>): Result<Unit> {
        return try {

            val wordsToSave = appDatabase.wordDao().getByIds(wordsIds.toList())

            if (wordsToSave.isEmpty()) {
                return Result.failure(Exception("Слова для сохранения не найдены в основной базе данных."))
            }


            userRepository.addWordFromMainDb(wordsToSave)

            Result.success(Unit)

        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    suspend operator fun invoke(wordsIds: Set<Int>) = execute(wordsIds)
}