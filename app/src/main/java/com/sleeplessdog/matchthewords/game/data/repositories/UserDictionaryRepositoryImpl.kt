package com.sleeplessdog.matchthewords.game.data.repositories

import com.sleeplessdog.matchthewords.game.data.UserWordEntity
import com.sleeplessdog.matchthewords.game.data.database.UserDictionaryDao
import kotlinx.coroutines.flow.Flow

class UserDictionaryRepository(private val dao: UserDictionaryDao) {

    fun getAllWords() = dao.getAllUserWords()

    suspend fun saveWord(word: UserWordEntity) {
        dao.insertOrUpdateWord(word)
    }

    //переписать корректно под word из базы данных
    suspend fun addWordFromMainDb(
        lang1Code: String,
        word1: String,
        lang2Code: String,
        word2: String,
        category: String? = null,
    ) {

        val englishWord = if (lang1Code == "en") word1 else if (lang2Code == "en") word2 else null

        if (englishWord == null) {
            val newWord = buildWordEntity(lang1Code, word1, lang2Code, word2, category)
            dao.insertOrUpdateWord(newWord)
            return
        }

        val existingWord = dao.findWordByEnglish(englishWord)

        if (existingWord == null) {
            val newWord = buildWordEntity(lang1Code, word1, lang2Code, word2, category)
            dao.insertOrUpdateWord(newWord)
        } else {

            val updatedWord = existingWord.copy(
                spanish = existingWord.spanish
                    ?: if (lang1Code == "es") word1 else if (lang2Code == "es") word2 else null,
                russian = existingWord.russian
                    ?: if (lang1Code == "ru") word1 else if (lang2Code == "ru") word2 else null,
                french = existingWord.french
                    ?: if (lang1Code == "fr") word1 else if (lang2Code == "fr") word2 else null,
                german = existingWord.german
                    ?: if (lang1Code == "de") word1 else if (lang2Code == "de") word2 else null
                // и так далее для других языков
            )
            dao.insertOrUpdateWord(updatedWord)
        }
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
            date = null // или другое значение по умолчанию
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