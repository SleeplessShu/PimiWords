package com.sleeplessdog.pimi.games.data.repository

import android.util.Log
import com.sleeplessdog.pimi.database.AppDatabaseProvider
import com.sleeplessdog.pimi.database.user.UserGroupEntity
import com.sleeplessdog.pimi.database.user.UserWordEntity
import com.sleeplessdog.pimi.dictionary.group_screen.WordUi
import com.sleeplessdog.pimi.dictionary.word_packs.WordPackEntry
import com.sleeplessdog.pimi.games.domain.models.CombinedWord
import com.sleeplessdog.pimi.games.domain.models.MutableWordBuilder
import com.sleeplessdog.pimi.games.domain.models.WordsGroupsList
import com.sleeplessdog.pimi.games.domain.models.set
import com.sleeplessdog.pimi.games.presentation.models.Word
import com.sleeplessdog.pimi.settings.Language
import com.sleeplessdog.pimi.settings.LanguageLevel
import com.sleeplessdog.pimi.utils.ConstantsPaths
import com.sleeplessdog.pimi.utils.SupportFunctions

class WordsRepository(
    private val databaseProvider: AppDatabaseProvider,
    private val appPrefs: AppPrefs,
) {

    suspend fun getWordPairs(
        lang1: Language,
        lang2: Language,
        levels: Set<LanguageLevel>,
        wordsNeeded: Int,
        categories: Set<String>,
    ): List<Pair<Word, Word>> {
        val globalDao = databaseProvider.getGlobalDatabase().globalDao()
        val userDao = databaseProvider.getUserDatabase().userDao()

        val globalGroupKeys = WordsGroupsList.values().map { it.key }.toSet()
        val globalCategoryKeys = categories.filter { it in globalGroupKeys }.toSet()
        val userGroupKeys = categories.filter {
            it !in globalGroupKeys
                    && it != WordsGroupsList.RANDOM.toString()
        }.toSet()
        val isRandom = categories.contains(WordsGroupsList.RANDOM.toString())
                || categories.isEmpty()

        val useArmTranslit = appPrefs.getArmScript().not()

        Log.d("REPO_DEBUG", "categories: $categories")
        Log.d("REPO_DEBUG", "globalGroupKeys sample: ${globalGroupKeys.take(5)}")
        Log.d("REPO_DEBUG", "globalCategoryKeys: $globalCategoryKeys")
        Log.d("REPO_DEBUG", "userGroupKeys: $userGroupKeys")
        Log.d("REPO_DEBUG", "isRandom: $isRandom")
        Log.d("REPO_DEBUG", "levels: $levels")
        Log.d("REPO_DEBUG", "useArmTranslit: $useArmTranslit")


        val globalWords = when {
            isRandom && globalCategoryKeys.isEmpty() -> globalDao.getWordsWOGroups(levels)
            globalCategoryKeys.isNotEmpty() -> globalDao.getWordsWGroups(levels, globalCategoryKeys)
            else -> emptyList()
        }

        Log.d("REPO_DEBUG", "globalWords count: ${globalWords.size}")

        val userGroupWords = if (userGroupKeys.isNotEmpty()) {
            userGroupKeys.flatMap { groupKey ->
                userDao.getWordsByGroupKey(groupKey)
            }
        } else {
            emptyList()
        }

        val userAllWords = userDao.getAllWords()
        val userByGlobalId = userAllWords.filter { it.globalId != null }
            .associateBy { it.globalId!! }

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
                serbian = u?.serbian ?: g.serbian,
                armTranslit = g.armTranslit,
            )
        }

        val mergedUserGroup = userGroupWords.map { u ->
            CombinedWord(
                globalId = null,
                userWordId = u.id,
                english = u.english,
                spanish = u.spanish,
                russian = u.russian,
                french = u.french,
                german = u.german,
                armenian = u.armenian,
                serbian = u.serbian,
            )
        }

        val pool = (mergedGlobal + mergedUserGroup).shuffled().take(wordsNeeded)

        return pool.mapNotNull { w ->
            val w1 = w.toWord(lang1, useArmTranslit = useArmTranslit)
            val w2 = w.toWord(lang2, useArmTranslit = useArmTranslit)
            if (!w1.isValid || !w2.isValid) null
            else w1 to w2
        }
    }

    suspend fun addGlobalWordsListToUserWords(globalIds: List<Int>) {

        val globalDao = databaseProvider.getGlobalDatabase().globalDao()
        val userDao = databaseProvider.getUserDatabase().userDao()

        if (globalIds.isEmpty()) return

        val savedGroup = getDefaultUserGroup()

        globalIds.distinct().forEach { id ->
            val globalId = id.toLong()
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
        appPrefs.markLocalDatabaseDirty()
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
        appPrefs.markLocalDatabaseDirty()
    }

    suspend fun addWordUserDB(
        groupId: String,
        origin: String,
        translate: String,
        originLanguage: Language,
        translateLanguage: Language,
    ) {
        val userDao = databaseProvider.getUserDatabase().userDao()
        val sanitizedOrigin = SupportFunctions.sanitizeInput(origin)
        val sanitizedTranslate = SupportFunctions.sanitizeInput(translate)
        val fields = MutableWordBuilder()

        fields.set(originLanguage, sanitizedOrigin)
        fields.set(translateLanguage, sanitizedTranslate)

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
        appPrefs.markLocalDatabaseDirty()
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

        val sanitizedOrigin = SupportFunctions.sanitizeInput(origin)
        val sanitizedTranslate = SupportFunctions.sanitizeInput(translate)

        val fields = MutableWordBuilder()

        fields.set(originLanguage, sanitizedOrigin)
        fields.set(translateLanguage, sanitizedTranslate)

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
        appPrefs.markLocalDatabaseDirty()
    }

    suspend fun deleteWord(groupId: String, wordId: Long) {
        val userDao = databaseProvider.getUserDatabase().userDao()
        userDao.deleteWordByGroupIdAndWordId(groupId, wordId)
        appPrefs.markLocalDatabaseDirty()
    }

    suspend fun moveWord(wordId: Long, targetGroupId: String) {
        val userDao = databaseProvider.getUserDatabase().userDao()
        userDao.moveWordToGroup(wordId, targetGroupId)
        appPrefs.markLocalDatabaseDirty()
    }

    private fun CombinedWord.toWord(language: Language, useArmTranslit: Boolean = false): Word {
        val text = when (language) {
            Language.ENGLISH -> english
            Language.SPANISH -> spanish
            Language.RUSSIAN -> russian
            Language.FRENCH -> french
            Language.GERMAN -> german
            Language.ARMENIAN -> if (useArmTranslit) armTranslit ?: armenian else armenian
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
                serbian = u.serbian,
            )
        }.shuffled().take(wordsNeeded)

        return pool.mapNotNull { w ->
            val w1 = w.toWord(lang1)
            val w2 = w.toWord(lang2)
            if (!w1.isValid || !w2.isValid) null
            else w1 to w2
        }
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
        appPrefs.markLocalDatabaseDirty()
    }
}
