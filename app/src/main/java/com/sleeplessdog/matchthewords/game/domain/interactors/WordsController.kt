package com.sleeplessdog.matchthewords.game.domain.interactors

import android.util.Log
import com.sleeplessdog.matchthewords.game.data.WordEntity
import com.sleeplessdog.matchthewords.game.data.repositories.WordsDatabase
import com.sleeplessdog.matchthewords.game.domain.models.LanguageLevel
import com.sleeplessdog.matchthewords.game.domain.models.WordsCategoriesList
import com.sleeplessdog.matchthewords.game.presentation.models.Language
import com.sleeplessdog.matchthewords.game.presentation.models.SessionStats
import com.sleeplessdog.matchthewords.game.presentation.models.Word
import com.sleeplessdog.matchthewords.utils.ConstantsApp

class WordsController(private val repository: WordsDatabase) {

    suspend fun getWordPairs(
        language1: Language,
        language2: Language,
        levels: Set<LanguageLevel>,
        wordsNeeded: Int,
        categories: Set<WordsCategoriesList>
    ): List<Pair<Word, Word>> {
        Log.d("DEBUG", "controller: $categories, $levels,  $wordsNeeded")
        val wordsList = repository.getWordsPack(
            levels = levels, wordsNeeded = wordsNeeded, categories = categories
        )

        return wordsList.map { wordEntity ->
            toWordPair(wordEntity, language1, language2)
        }
    }

    fun toWordPair(
        wordEntity: WordEntity, original: Language, translate: Language
    ): Pair<Word, Word> {
        val word1 = getWordForLanguage(wordEntity, original)
        val word2 = getWordForLanguage(wordEntity, translate)
        return Pair(word1, word2)
    }

    fun getWordForLanguage(entity: WordEntity, lang: Language): Word {
        val id = entity.id ?: ConstantsApp.INVALID_ID

        return when (lang) {
            Language.ENGLISH -> Word(id, entity.english, Language.ENGLISH)
            Language.SPANISH -> wordOrInvalid(id, entity.spanish, Language.SPANISH)
            Language.RUSSIAN -> wordOrInvalid(id, entity.russian, Language.RUSSIAN)
            Language.FRENCH  -> wordOrInvalid(id, entity.french, Language.FRENCH)
            Language.GERMAN  -> wordOrInvalid(id, entity.german, Language.GERMAN)
        }
    }

    fun putRoundStats(stats: SessionStats) {
        Log.d("DEBUG", "CorrectIds: ${stats.correctIds}")
        Log.d("DEBUG", "MistakeIds: ${stats.mistakeIds}")
    }

    private fun wordOrInvalid(
        id: Int,
        value: String?,
        lang: Language
    ): Word {
        return value?.let { Word(id, it, lang) }
            ?: Word.invalid(lang)
    }
}
