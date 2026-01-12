package com.sleeplessdog.matchthewords.game.data.repositories

import android.util.Log
import com.sleeplessdog.matchthewords.game.data.WordEntity
import com.sleeplessdog.matchthewords.game.data.database.WordDao
import com.sleeplessdog.matchthewords.game.domain.models.LanguageLevel
import com.sleeplessdog.matchthewords.game.domain.models.WordsCategoriesList

class AppDictionaryRepository(private val wordDao: WordDao) {

    suspend fun getWordsPack(
        levels: Set<LanguageLevel>, wordsNeeded: Int, categories: Set<WordsCategoriesList>,
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
