package com.sleeplessdog.matchthewords.game.data.repositories

import com.sleeplessdog.matchthewords.game.data.UserWordEntity
import com.sleeplessdog.matchthewords.game.data.WordEntity
import com.sleeplessdog.matchthewords.game.data.database.UserDictionaryDao
import com.sleeplessdog.matchthewords.utils.SupportFunctions
import kotlinx.coroutines.flow.Flow

class UserDictionaryRepository(private val dao: UserDictionaryDao) {

    fun getAllWords() = dao.getAllUserWords()

    suspend fun saveWord(word: List<UserWordEntity>) {
        dao.insertOrUpdateWord(word)
    }

    suspend fun addWordFromMainDb(wordsToSave: List<WordEntity>) {
        if (wordsToSave.isEmpty()) {
            return
        }

        val userWordsToSave = wordsToSave.map { wordEntity ->
            UserWordEntity(
                english = wordEntity.english,
                spanish = wordEntity.spanish,
                russian = wordEntity.russian,
                french = wordEntity.french,
                german = wordEntity.german,
                category = wordEntity.category,
                dateLastSeen = SupportFunctions.getCurrentDate(),
                dateAdded = SupportFunctions.getCurrentDate()
            )
        }

        // 2. Сохраняем все подготовленные слова в базу данных одним вызовом
        dao.insertOrUpdateWord(userWordsToSave)
    }


    private fun buildWordEntity(
        lang1Code: String,
        word1: String,
        lang2Code: String,
        word2: String,
        category: String?,
    ): UserWordEntity {
        val wordsMap = mapOf(lang1Code to word1, lang2Code to word2)
        return UserWordEntity(
            english = wordsMap["en"],
            spanish = wordsMap["es"],
            russian = wordsMap["ru"],
            french = wordsMap["fr"],
            german = wordsMap["de"],
            category = category,
            dateLastSeen = SupportFunctions.getCurrentDate(),
            dateAdded = SupportFunctions.getCurrentDate(),
        )
    }


    suspend fun deleteUserWord(word: UserWordEntity) {
        dao.deleteWord(word)
    }

    suspend fun incrementCorrectAnswers(wordId: Long) {
        val word = dao.getWordById(wordId)
        word?.let {
            val updatedWord = it.copy(correct = (it.correct ?: 0) + 1)
            dao.updateWord(updatedWord)
        }
    }

    suspend fun incrementMistakeAnswers(wordId: Long) {
        val word = dao.getWordById(wordId)
        word?.let {
            val updatedWord = it.copy(mistake = (it.mistake ?: 0) + 1)
            dao.updateWord(updatedWord)
        }
    }

    fun getWordsByCategory(categoryName: String): Flow<List<UserWordEntity>> {
        return dao.getWordsByCategory(categoryName)
    }
}