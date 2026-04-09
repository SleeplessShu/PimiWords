package com.sleeplessdog.pimi.games.data.repository

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.sleeplessdog.pimi.settings.DifficultyLevel
import com.sleeplessdog.pimi.settings.Language
import com.sleeplessdog.pimi.settings.LanguageLevel
import com.sleeplessdog.pimi.utils.ConstantsPaths
import com.sleeplessdog.pimi.utils.ConstantsPaths.IS_PREMIUM
import com.sleeplessdog.pimi.utils.ConstantsPaths.KEY_DIFFICULTY
import com.sleeplessdog.pimi.utils.ConstantsPaths.KEY_LEVELS
import com.sleeplessdog.pimi.utils.ConstantsPaths.KEY_STUDY_LANG
import com.sleeplessdog.pimi.utils.ConstantsPaths.KEY_UI_LANG
import com.sleeplessdog.pimi.utils.ConstantsPaths.PREFS_NAME
import com.sleeplessdog.pimi.utils.ConstantsPaths.PREMIUM_STATUS
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.util.Locale

interface AppPrefs {

    fun observeUiLanguage(): Flow<Language>
    fun observeStudyLanguage(): Flow<Language>
    fun getUiLanguage(): Language
    fun getStudyLanguage(): Language
    fun save(ui: Language, study: Language)

    fun getLevels(): Set<LanguageLevel>
    fun saveLevels(levels: Set<LanguageLevel>)

    fun getDifficulty(): DifficultyLevel
    fun saveDifficulty(level: DifficultyLevel)

    fun setPremium(value: Boolean)
    fun isPremium(): Boolean

    fun markLocalDatabaseDirty()
    fun markLocalDatabaseClear()
    fun getLocalDatabaseDirty(): Boolean
}

class AppPrefsImpl(
    context: Context,
) : AppPrefs {

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun observeUiLanguage(): Flow<Language> = callbackFlow {

        trySend(getUiLanguage())

        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == KEY_UI_LANG) {
                trySend(getUiLanguage())
            }
        }

        prefs.registerOnSharedPreferenceChangeListener(listener)
        awaitClose { prefs.unregisterOnSharedPreferenceChangeListener(listener) }
    }

    override fun observeStudyLanguage(): Flow<Language> = callbackFlow {
        trySend(getStudyLanguage())

        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == KEY_STUDY_LANG) {
                trySend(getStudyLanguage())
            }
        }
        prefs.registerOnSharedPreferenceChangeListener(listener)
        awaitClose { prefs.unregisterOnSharedPreferenceChangeListener(listener) }
    }

    override fun getUiLanguage(): Language {
        val stored = prefs.getString(KEY_UI_LANG, null)
        if (stored != null) return Language.valueOf(stored)

        val systemLocale = Locale.getDefault().language
        val detected =
            Language.entries.find { it.toLocale().language == systemLocale } ?: Language.ENGLISH

        prefs.edit().putString(KEY_UI_LANG, detected.name).apply()
        return detected
    }

    override fun getStudyLanguage(): Language {
        val name = prefs.getString(KEY_STUDY_LANG, Language.ENGLISH.name)
        return safeLang(name, Language.ENGLISH)
    }

    override fun save(ui: Language, study: Language) {
        prefs.edit().putString(KEY_UI_LANG, ui.name).putString(KEY_STUDY_LANG, study.name).apply()
    }

    override fun getLevels(): Set<LanguageLevel> {
        val stored = prefs.getStringSet(KEY_LEVELS, null) ?: return setOf(LanguageLevel.A1)

        return stored.mapNotNull { safeLevel(it) }.toSet().ifEmpty { setOf(LanguageLevel.A1) }
    }

    override fun saveLevels(levels: Set<LanguageLevel>) {
        prefs.edit().putStringSet(KEY_LEVELS, levels.map { it.name }.toSet()).apply()
    }

    override fun getDifficulty(): DifficultyLevel {
        val name = prefs.getString(KEY_DIFFICULTY, DifficultyLevel.EASY.name)
        return safeDifficulty(name, DifficultyLevel.EASY)
    }

    override fun saveDifficulty(level: DifficultyLevel) {
        prefs.edit().putString(KEY_DIFFICULTY, level.name).apply()
    }

    private fun safeLang(value: String?, fallback: Language): Language = try {
        Language.valueOf(value ?: "")
    } catch (_: Exception) {
        fallback
    }

    private fun safeLevel(value: String?): LanguageLevel? = try {
        LanguageLevel.valueOf(value ?: "")
    } catch (_: Exception) {
        null
    }

    private fun safeDifficulty(value: String?, fallback: DifficultyLevel): DifficultyLevel = try {
        DifficultyLevel.valueOf(value ?: "")
    } catch (_: Exception) {
        fallback
    }

    override fun setPremium(value: Boolean) {
        prefs.edit().putBoolean(PREMIUM_STATUS, value).apply()
    }

    override fun isPremium(): Boolean = prefs.getBoolean(IS_PREMIUM, false)

    override fun markLocalDatabaseDirty() {
        Log.d("Preference repo", "markLocalDatabaseDirty: true")
        prefs.edit()
            .putLong(ConstantsPaths.USER_DATABASE_DICTIONARY_DATE, System.currentTimeMillis())
            .apply()
        prefs.edit().putBoolean(ConstantsPaths.USER_DATABASE_DICTIONARY_IS_DIRTY, true).apply()

    }

    override fun markLocalDatabaseClear() {
        Log.d("Preference repo", "markLocalDatabaseDirty: false")
        prefs.edit().putBoolean(ConstantsPaths.USER_DATABASE_DICTIONARY_IS_DIRTY, false).apply()

    }

    override fun getLocalDatabaseDirty(): Boolean {
        return prefs.getBoolean(ConstantsPaths.USER_DATABASE_DICTIONARY_IS_DIRTY, false)
    }
}

