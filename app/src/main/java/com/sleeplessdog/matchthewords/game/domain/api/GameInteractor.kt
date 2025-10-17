package com.sleeplessdog.matchthewords.game.domain.api

import com.sleeplessdog.matchthewords.game.data.WordEntity
import com.sleeplessdog.matchthewords.game.domain.models.LanguageLevel
import com.sleeplessdog.matchthewords.game.domain.models.WordCategory
import com.sleeplessdog.matchthewords.game.presentation.models.Language
import com.sleeplessdog.matchthewords.game.presentation.models.SessionStats
import com.sleeplessdog.matchthewords.game.presentation.models.Word


interface GameInteractor {

    suspend fun getWordPairs(
        language1: Language,
        language2: Language,
        level: LanguageLevel,
        difficultLevel: Int,
        category: WordCategory
    ): List<Pair<Word, Word>>

    fun toWordPair(
        wordEntity: WordEntity,
        original: Language,
        translate: Language
    ): Pair<Word, Word>

    fun getWordForLanguage(entity: WordEntity, lang: Language): Word

    fun putRoundStats(stats: SessionStats){}
}