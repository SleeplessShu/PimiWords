package com.sleeplessdog.pimi.game.data.repositories

import android.util.Log
import com.sleeplessdog.pimi.game.data.WordEntity
import com.sleeplessdog.pimi.game.data.database.WordDao
import com.sleeplessdog.pimi.game.domain.models.LanguageLevel
import com.sleeplessdog.pimi.game.domain.models.WordsGroupsList

class AppDictionaryRepository(private val wordDao: WordDao) {

    suspend fun getWordsPack(
        levels: Set<LanguageLevel>, wordsNeeded: Int, categories: Set<WordsGroupsList>,
    ): List<WordEntity> {
        Log.d("DEBUG", "getWordsPack: ${levels}  $categories")
        val isAnyLevel = levels.isEmpty()
        val isAnyCategory = categories.isEmpty()

        val levelNames = levels.map { it.name }
        val categoryNames = categories.map { it.key }

        val fromDb: List<WordEntity> = when {
            isAnyCategory && isAnyLevel -> wordDao.getAny(wordsNeeded)

            isAnyCategory -> wordDao.getAllCategoriesByLevels(levelNames)

            isAnyLevel -> wordDao.getByCategoriesAllLevels(categoryNames)

            else -> wordDao.getByCategoriesAndLevels(categoryNames, levelNames)
        }
        return adaptForConditions(fromDb, wordsNeeded)
    }


    private suspend fun getRandom(wordsNeeded: Int): List<WordEntity> =
        wordDao.getRandom(wordsNeeded)

    private suspend fun getSizeOfCategory(category: String): Int {
        val allWords = wordDao.getAllWordsOfCategory(category)
        return allWords.size
    }


    suspend fun updateUsedWordsStatistic(wordEntity: WordEntity) {
        wordDao.updateWord(wordEntity)
    }

    private suspend fun adaptForConditions(
        dataBaseResponse: List<WordEntity>, difficultLevel: Int,
    ): List<WordEntity> {
        if (difficultLevel <= 0) return emptyList()

        val shuffled = dataBaseResponse.shuffled()

        return if (shuffled.size >= difficultLevel) {
            shuffled.take(difficultLevel)
        } else {
            val missing = difficultLevel - shuffled.size
            val additional = getRandom(missing)
            (shuffled + additional).take(difficultLevel)
        }
    }
}
