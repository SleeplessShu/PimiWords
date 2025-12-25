package com.sleeplessdog.matchthewords.game.data.repositories

import com.sleeplessdog.matchthewords.game.data.UserWordEntity
import com.sleeplessdog.matchthewords.game.data.database.UserDictionaryDao

class UserDictionaryRepository(private val dao: UserDictionaryDao) {

    fun getAllWords() = dao.getAllUserWords()

    suspend fun addWord(word: UserWordEntity) {
        dao.insertWord(word)
    }


    suspend fun addWordFromMainDb(
        originalWord: String,
        translatedWord: String,
        sourceLang: String,
        targetLang: String,
    ) {

        val existingWord = dao.findWord(originalWord, sourceLang, targetLang)
        if (existingWord == null) {
            val newUserWord = UserWordEntity(
                originalWord = originalWord,
                translatedWord = translatedWord,
                sourceLang = sourceLang,
                targetLang = targetLang
            )
            dao.insertWord(newUserWord)
        }
    }

    suspend fun updateUserWord(word: UserWordEntity) {
        dao.updateWord(word)
    }

    suspend fun deleteUserWord(word: UserWordEntity) {
        dao.deleteWord(word)
    }
}