package com.sleeplessdog.pimi.games.domain.models

import com.sleeplessdog.pimi.games.data.repository.WordsRepository
import com.sleeplessdog.pimi.settings.Language
import com.sleeplessdog.pimi.games.presentation.models.Word
import com.sleeplessdog.pimi.settings.LanguageLevel

class WordsController(
    private val repository: WordsRepository,
) {

    suspend fun getWordPairs(
        language1: Language,
        language2: Language,
        levels: Set<LanguageLevel>,
        wordsNeeded: Int,
        categories: Set<WordsGroupsList>,
    ): List<Pair<Word, Word>> {

        // защита от мусорных вызовов
        if (wordsNeeded <= 0) return emptyList()
        if (language1 == language2) return emptyList()

        return repository.getWordPairs(
            lang1 = language1,
            lang2 = language2,
            levels = levels,
            wordsNeeded = wordsNeeded,
            categories = categories
        )
    }
}
