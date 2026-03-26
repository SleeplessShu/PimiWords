package com.sleeplessdog.matchthewords.backend.data.repository

import com.sleeplessdog.matchthewords.backend.data.db.AppDatabaseProvider
import com.sleeplessdog.matchthewords.backend.data.db.global.GlobalDictionaryEntity
import com.sleeplessdog.matchthewords.backend.data.db.user.UserGroupEntity
import com.sleeplessdog.matchthewords.backend.data.db.user.UserWordEntity
import com.sleeplessdog.matchthewords.backend.domain.models.CombinedWord
import com.sleeplessdog.matchthewords.backend.domain.models.LanguageLevel
import com.sleeplessdog.matchthewords.backend.domain.models.MutableWordBuilder
import com.sleeplessdog.matchthewords.backend.domain.models.WordsGroupsList
import com.sleeplessdog.matchthewords.backend.domain.models.set
import com.sleeplessdog.matchthewords.dictionary.group_screen.WordUi
import com.sleeplessdog.matchthewords.dictionary.models.WordPackEntry
import com.sleeplessdog.matchthewords.game.presentation.models.Language
import com.sleeplessdog.matchthewords.game.presentation.models.Word
import com.sleeplessdog.matchthewords.utils.ConstantsPaths

class WordsRepository(
    private val databaseProvider: AppDatabaseProvider,
) {
    /**
     * возвращает список пар слов для игры
     */
    suspend fun getWordPairs(
        lang1: Language,
        lang2: Language,
        levels: Set<LanguageLevel>,
        wordsNeeded: Int,
        categories: Set<WordsGroupsList>,
    ): List<Pair<Word, Word>> {
        val globalDao = databaseProvider.getGlobalDatabase().globalDao()
        val userDao = databaseProvider.getUserDatabase().userDao()

        val categoryKeys = categories.filter { it != WordsGroupsList.RANDOM }.map { it.key }.toSet()
            .takeIf { it.isNotEmpty() }

        //Global words
        var globalWords = emptyList<GlobalDictionaryEntity>()
        if (categoryKeys.isNullOrEmpty()) {
            globalWords = globalDao.getWordsWOGroups(levels)
        } else {
            globalWords = globalDao.getWordsWGroups(levels, categoryKeys)
        }

        //User words (включая user-only)
        val userWords = userDao.getAllWords()

        val userByGlobalId = userWords.filter { it.globalId != null }.associateBy { it.globalId!! }

        val userOnlyWords = userWords.filter { it.globalId == null }

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
        val pool = (mergedGlobal + mergedUserOnly).shuffled().take(wordsNeeded)

        //Word пары
        return pool.mapNotNull { w ->
            val w1 = w.toWord(lang1)
            val w2 = w.toWord(lang2)

            if (!w1.isValid || !w2.isValid) null
            else w1 to w2
        }
    }


    /**
     * для сохранения слов в конце игры
     */
    suspend fun addGlobalWordsListToUserWords(globalIds: List<Int>) {

        val globalDao = databaseProvider.getGlobalDatabase().globalDao()
        val userDao = databaseProvider.getUserDatabase().userDao()

        if (globalIds.isEmpty()) return

        val savedGroup = getDefaultUserGroup()

        globalIds.distinct().forEach { id ->
            val globalId = id.toLong()

            // если слово уже сохранено — пропускаем
            val exists = userDao.findByGlobalId(globalId)
            val globalEntity = globalDao.getById(globalId)
            if (exists != null) return@forEach

            userDao.insertWord(
                UserWordEntity(
                    globalId = globalId,
                    groupId = savedGroup.groupKey,
                    english = globalEntity?.english,
                    spanish = globalEntity?.spanish,
                    russian = globalEntity?.russian,
                    french = globalEntity?.french,
                    german = globalEntity?.german,
                    armenian = globalEntity?.armenian,
                    serbian = globalEntity?.serbian
                )
            )
        }
    }

    suspend fun addSingleWordToSavedWordsUC(word: WordUi) {

        val globalDao = databaseProvider.getGlobalDatabase().globalDao()
        val userDao = databaseProvider.getUserDatabase().userDao()

        val savedGroup = getDefaultUserGroup()
        val globalEntity = globalDao.getById(word.id)
        userDao.insertWord(
            UserWordEntity(
                globalId = word.id,
                groupId = savedGroup.groupKey,
                english = globalEntity?.english,
                spanish = globalEntity?.spanish,
                russian = globalEntity?.russian,
                french = globalEntity?.french,
                german = globalEntity?.german,
                armenian = globalEntity?.armenian,
                serbian = globalEntity?.serbian
            )
        )
    }

    suspend fun addWordUserDB(
        groupId: String,
        origin: String,
        translate: String,
        originLanguage: Language,
        translateLanguage: Language,
    ) {
        val userDao = databaseProvider.getUserDatabase().userDao()

        val fields = MutableWordBuilder()

        fields.set(originLanguage, origin)
        fields.set(translateLanguage, translate)

        userDao.insertWord(
            UserWordEntity(
                groupId = groupId,
                globalId = null,
                english = fields.english,
                spanish = fields.spanish,
                russian = fields.russian,
                french = fields.french,
                german = fields.german,
                armenian = fields.armenian,
                serbian = fields.serbian
            )
        )
    }

    suspend fun editWordUserDB(
        groupId: String,
        wordId: Long,
        origin: String,
        translate: String,
        originLanguage: Language,
        translateLanguage: Language,
    ) {
        val userDao = databaseProvider.getUserDatabase().userDao()

        val fields = MutableWordBuilder()

        fields.set(originLanguage, origin)
        fields.set(translateLanguage, translate)

        userDao.updateUserWordFields(
            wordId = wordId,
            groupId = groupId,
            english = fields.english,
            spanish = fields.spanish,
            russian = fields.russian,
            french = fields.french,
            german = fields.german,
            armenian = fields.armenian,
            serbian = fields.serbian
        )
    }

    suspend fun deleteWord(groupId: String, wordId: Long) {
        val userDao = databaseProvider.getUserDatabase().userDao()
        userDao.deleteWordByGroupIdAndWordId(groupId, wordId)
    }

    suspend fun moveWord(wordId: Long, targetGroupId: String) {
        val userDao = databaseProvider.getUserDatabase().userDao()
        userDao.moveWordToGroup(wordId, targetGroupId)
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

    suspend fun getWordPairsFromUserGroup(
        lang1: Language,
        lang2: Language,
        groupKey: String,
        wordsNeeded: Int,
    ): List<Pair<Word, Word>> {
        val userDao = databaseProvider.getUserDatabase().userDao()

        val userWords = userDao.getWordsByGroupKey(groupKey)

        val pool = userWords.map { u ->
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
        }.shuffled().take(wordsNeeded)

        return pool.mapNotNull { w ->
            val w1 = w.toWord(lang1)
            val w2 = w.toWord(lang2)
            if (!w1.isValid || !w2.isValid) null
            else w1 to w2
        }
    }

    fun valueFor(language: Language, value: String?): String? = when (language) {
        Language.ENGLISH -> value
        Language.SPANISH -> value
        Language.RUSSIAN -> value
        Language.FRENCH -> value
        Language.GERMAN -> value
        Language.ARMENIAN -> value
        Language.SERBIAN -> value
    }

    private suspend fun getDefaultUserGroup(): UserGroupEntity {
        val userDao = databaseProvider.getUserDatabase().userDao()
        return userDao.getGroupByKey(ConstantsPaths.SAVED_GROUP_KEY)
            ?: error("Saved words group not found")
    }

    suspend fun addWordPackEntry(groupId: String, entry: WordPackEntry) {
        val userDao = databaseProvider.getUserDatabase().userDao()
        userDao.insertWord(
            UserWordEntity(
                groupId = groupId,
                globalId = null,
                english = entry.english,
                russian = entry.russian,
                serbian = entry.serbian,
                spanish = entry.spanish,
                french = entry.french,
                german = entry.german,
                armenian = entry.armenian,
            )
        )
    }
}
