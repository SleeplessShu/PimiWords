package com.sleeplessdog.matchthewords.game.domain.interactors

import android.util.Log
import com.sleeplessdog.matchthewords.game.data.WordEntity
import com.sleeplessdog.matchthewords.game.data.repositories.AppDictionaryRepository
import com.sleeplessdog.matchthewords.game.domain.models.LanguageLevel
import com.sleeplessdog.matchthewords.game.domain.models.WordsCategoriesList
import com.sleeplessdog.matchthewords.game.presentation.models.Language
import com.sleeplessdog.matchthewords.game.presentation.models.SessionStats
import com.sleeplessdog.matchthewords.game.presentation.models.Word

class WordsController(private val repository: AppDictionaryRepository) {

    suspend fun getWordPairs(
        language1: Language,
        language2: Language,
        levels: Set<LanguageLevel>,
        wordsNeeded: Int,
        categories: Set<WordsCategoriesList>,
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
        wordEntity: WordEntity, original: Language, translate: Language,
    ): Pair<Word, Word> {
        val word1 = getWordForLanguage(wordEntity, original)
        val word2 = getWordForLanguage(wordEntity, translate)
        return Pair(word1, word2)
    }

    fun getWordForLanguage(entity: WordEntity, lang: Language): Word {
        val id = entity.id ?: -1
        return when (lang) {
            Language.ENGLISH -> Word(id, entity.english, Language.ENGLISH)
            Language.SPANISH -> entity.spanish?.let { Word(id, it, Language.SPANISH) }
                ?: Word.invalid(Language.SPANISH)

            Language.RUSSIAN -> entity.russian?.let { Word(id, it, Language.RUSSIAN) }
                ?: Word.invalid(Language.RUSSIAN)

            Language.FRENCH -> entity.french?.let { Word(id, it, Language.FRENCH) } ?: Word.invalid(
                Language.FRENCH
            )

            Language.GERMAN -> entity.german?.let { Word(id, it, Language.GERMAN) } ?: Word.invalid(
                Language.GERMAN
            )
        }
    }

    fun putRoundStats(stats: SessionStats) {
        Log.d("DEBUG", "CorrectIds: ${stats.correctIds}")
        Log.d("DEBUG", "MistakeIds: ${stats.mistakeIds}")
    }
}
