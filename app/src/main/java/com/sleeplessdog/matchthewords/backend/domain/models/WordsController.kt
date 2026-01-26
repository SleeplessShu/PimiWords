package com.sleeplessdog.matchthewords.backend.domain.models

import com.sleeplessdog.matchthewords.backend.data.repository.WordsRepository
import com.sleeplessdog.matchthewords.backend.domain.models.LanguageLevel
import com.sleeplessdog.matchthewords.backend.domain.models.WordsGroupsList
import com.sleeplessdog.matchthewords.game.presentation.models.Language
import com.sleeplessdog.matchthewords.game.presentation.models.Word

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
