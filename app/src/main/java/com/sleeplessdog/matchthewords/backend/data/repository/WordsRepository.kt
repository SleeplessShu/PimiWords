package com.sleeplessdog.matchthewords.backend.data.repository

import com.sleeplessdog.matchthewords.backend.data.db.global.GlobalDao
import com.sleeplessdog.matchthewords.backend.data.db.user.UserDao
import com.sleeplessdog.matchthewords.backend.data.db.user.UserWordEntity
import com.sleeplessdog.matchthewords.backend.domain.models.CombinedWord
import com.sleeplessdog.matchthewords.backend.domain.models.LanguageLevel
import com.sleeplessdog.matchthewords.backend.domain.models.WordsGroupsList
import com.sleeplessdog.matchthewords.game.presentation.models.Language
import com.sleeplessdog.matchthewords.game.presentation.models.Word

class WordsRepository(
    private val globalDao: GlobalDao,
    private val userDao: UserDao,
) {

    suspend fun getWordPairs(
        lang1: Language,
        lang2: Language,
        levels: Set<LanguageLevel>,
        wordsNeeded: Int,
        categories: Set<WordsGroupsList>,
    ): List<Pair<Word, Word>> {

        val categoryKeys = categories
            .filter { it != WordsGroupsList.RANDOM }
            .map { it.key }
            .toSet()
            .takeIf { it.isNotEmpty() }

        //Global words
        val globalWords = globalDao.getWords(
            levels = levels,
            groupKeys = categoryKeys
        )

        //User words (включая user-only)
        val userWords = userDao.getAllWords()

        val userByGlobalId = userWords
            .filter { it.globalId != null }
            .associateBy { it.globalId!! }

        val userOnlyWords = userWords
            .filter { it.globalId == null }

        //Merge global + user overrides
        val mergedGlobal = globalWords.map { g ->
            val u = userByGlobalId[g.id]

            CombinedWord(
                globalId = g.id,
                userWordId = u?.id,
                english = u?.english ?: g.english,
                spanish = u?.spanish ?: g.spanish,
                russian = u?.russian ?: g.russian,
                french = u?.french ?: g.french,
                german = u?.german ?: g.german,
                armenian = u?.armenian ?: g.armenian,
                serbian = u?.serbian ?: g.serbian
            )
        }

        //User-only → CombinedWord
        val mergedUserOnly = userOnlyWords.map { u ->
            CombinedWord(
                globalId = null,
                userWordId = u.id,
                english = u.english,
                spanish = u.spanish,
                russian = u.russian,
                french = u.french,
                german = u.german,
                armenian = u.armenian,
                serbian = u.serbian
            )
        }

        //Итоговый пул
        val pool = (mergedGlobal + mergedUserOnly)
            .shuffled()
            .take(wordsNeeded)

        //Word пары
        return pool.mapNotNull { w ->
            val w1 = w.toWord(lang1)
            val w2 = w.toWord(lang2)

            if (!w1.isValid || !w2.isValid) null
            else w1 to w2
        }
    }

    suspend fun getWordsByGroup(groupKey: String): List<UserWordEntity> {
        val group = userDao.getGroupByKey(groupKey) ?: return emptyList()
        return userDao.getWordsByGroups(setOf(group.id))
    }

    // ---------- helpers ----------

    private fun CombinedWord.toWord(language: Language): Word {
        val text = when (language) {
            Language.ENGLISH -> english
            Language.SPANISH -> spanish
            Language.RUSSIAN -> russian
            Language.FRENCH -> french
            Language.GERMAN -> german
            Language.ARMENIAN -> armenian
            Language.SERBIAN -> serbian
        }

        return if (text.isNullOrBlank()) {
            Word.invalid(language)
        } else {
            Word(
                id = (globalId ?: -userWordId!!).toInt(),
                text = text,
                language = language,
                isValid = true
            )
        }
    }
}
