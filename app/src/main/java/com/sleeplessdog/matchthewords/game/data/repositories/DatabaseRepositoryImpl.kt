package com.sleeplessdog.matchthewords.game.data.repositories

import com.sleeplessdog.matchthewords.game.data.WordEntity
import com.sleeplessdog.matchthewords.game.data.database.WordDao
import com.sleeplessdog.matchthewords.game.domain.models.LanguageLevel
import com.sleeplessdog.matchthewords.game.domain.models.WordCategory
import com.sleeplessdog.matchthewords.game.domain.repositories.DatabaseRepository
import com.sleeplessdog.matchthewords.game.presentation.models.Language

class DatabaseRepositoryImpl(private val wordDao: WordDao) : DatabaseRepository {

    override suspend fun getWordsPack(
        language1: Language,
        language2: Language,
        level: LanguageLevel,
        difficultLevel: Int,
        category: WordCategory
    ): List<WordEntity> {

        val isAnyCategory = category == WordCategory.RANDOM
        val isAnyLevel = level == LanguageLevel.RANDOM

        val fromDb: List<WordEntity> = when {
            isAnyCategory && isAnyLevel -> wordDao.getAny(difficultLevel)

            isAnyCategory -> wordDao.getAllCategoriesByLevel(level.name)

            isAnyLevel -> wordDao.getByCategoryAllLevels(category.name)

            else -> wordDao.getByCategoryAndLevel(category.name, level.name)
        }

        return adaptForConditions(fromDb, difficultLevel)
    }

    private suspend fun getRandom(wordsNeeded: Int): List<WordEntity> =
        wordDao.getRandom(wordsNeeded)

    override suspend fun updateUsedWordsStatistic(wordEntity: WordEntity) {
        wordDao.updateWord(wordEntity)
    }

    private suspend fun adaptForConditions(
        dataBaseResponse: List<WordEntity>,
        difficultLevel: Int
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
