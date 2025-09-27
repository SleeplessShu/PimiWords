package com.sleeplessdog.matchthewords.game.domain.interactors

import android.util.Log
import com.sleeplessdog.matchthewords.game.data.WordEntity
import com.sleeplessdog.matchthewords.game.domain.api.DatabaseInteractor
import com.sleeplessdog.matchthewords.game.domain.api.GameInteractor
import com.sleeplessdog.matchthewords.game.domain.models.LanguageLevel
import com.sleeplessdog.matchthewords.game.domain.models.WordCategory
import com.sleeplessdog.matchthewords.game.presentation.models.Language
import com.sleeplessdog.matchthewords.game.presentation.models.Word

import kotlin.random.Random

class GameInteractorImpl(private val repository: DatabaseInteractor) : GameInteractor {

    override suspend fun getWordPairs(
        language1: Language,
        language2: Language,
        level: LanguageLevel,
        difficultLevel: Int,
        category: WordCategory
    ): List<Pair<Word, Word>> {
        val wordsList = repository.getWordsPack(language1, language2, level, difficultLevel, category)
        Log.d("GameInteractorTesting", "getWordPairs: ${wordsList}")
        return wordsList.map { wordEntity ->
            toWordPair(wordEntity, language1, language2)
        }
    }

    override fun shufflePairs(input: List<Pair<Word, Word>>): List<Pair<Word, Word>> {
        if (input.size <= 1) return input
        val secondWords = input.map { it.second }.shuffled(Random(System.currentTimeMillis()))
        return input.mapIndexed { index, pair ->
            pair.first to secondWords[index]
        }
    }

    override fun toWordPair(wordEntity: WordEntity, original: Language, translate: Language): Pair<Word, Word> {
        val word1 = getWordForLanguage(wordEntity, original)
        val word2 = getWordForLanguage(wordEntity, translate)
        return Pair(word1, word2)
    }

    override fun getWordForLanguage(entity: WordEntity, lang: Language): Word {
        val id = entity.id ?: -1
        return when (lang) {
            Language.ENGLISH -> Word(id, entity.english, Language.ENGLISH)
            Language.SPANISH -> entity.spanish?.let { Word(id, it, Language.SPANISH) }
                ?: Word.invalid(Language.SPANISH)
            Language.RUSSIAN -> entity.russian?.let { Word(id, it, Language.RUSSIAN) }
                ?: Word.invalid(Language.RUSSIAN)
            Language.FRENCH -> entity.french?.let { Word(id, it, Language.FRENCH) }
                ?: Word.invalid(Language.FRENCH)
            Language.GERMAN -> entity.german?.let { Word(id, it, Language.GERMAN) }
                ?: Word.invalid(Language.GERMAN)
        }
    }

}
