package com.sleeplessdog.matchthewords.game.data.repositories

import android.content.Context
import com.sleeplessdog.matchthewords.game.domain.models.LanguageLevel
import com.sleeplessdog.matchthewords.game.presentation.models.DifficultLevel
import com.sleeplessdog.matchthewords.game.presentation.models.Language

interface AppPrefs {
    fun getUiLanguage(): Language
    fun getStudyLanguage(): Language
    fun save(ui: Language, study: Language)

    fun getLevels(): Set<LanguageLevel>
    fun saveLevels(levels: Set<LanguageLevel>)

    fun getDifficulty(): DifficultLevel
    fun saveDifficulty(level: DifficultLevel)
}

class AppPrefsImpl(
    context: Context
) : AppPrefs {

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    // ---------- LANGS ----------
    override fun getUiLanguage(): Language {
        val name = prefs.getString(KEY_UI_LANG, Language.RUSSIAN.name)
        return safeLang(name, Language.RUSSIAN)
    }

    override fun getStudyLanguage(): Language {
        val name = prefs.getString(KEY_STUDY_LANG, Language.ENGLISH.name)
        return safeLang(name, Language.ENGLISH)
    }

    override fun save(ui: Language, study: Language) {
        prefs.edit().putString(KEY_UI_LANG, ui.name).putString(KEY_STUDY_LANG, study.name).apply()
    }

    // ---------- LEVELS ----------
    override fun getLevels(): Set<LanguageLevel> {
        val stored = prefs.getStringSet(KEY_LEVELS, null) ?: return setOf(LanguageLevel.A1)

        return stored.mapNotNull { safeLevel(it) }.toSet().ifEmpty { setOf(LanguageLevel.A1) }
    }

    override fun saveLevels(levels: Set<LanguageLevel>) {
        prefs.edit().putStringSet(KEY_LEVELS, levels.map { it.name }.toSet()).apply()
    }

    // ---------- DIFFICULTY ----------
    override fun getDifficulty(): DifficultLevel {
        val name = prefs.getString(KEY_DIFFICULTY, DifficultLevel.EASY.name)
        return safeDifficulty(name, DifficultLevel.EASY)
    }

    override fun saveDifficulty(level: DifficultLevel) {
        prefs.edit().putString(KEY_DIFFICULTY, level.name).apply()
    }

    // ---------- SAFE PARSERS ----------
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

    private fun safeDifficulty(value: String?, fallback: DifficultLevel): DifficultLevel = try {
        DifficultLevel.valueOf(value ?: "")
    } catch (_: Exception) {
        fallback
    }

    companion object {
        private const val PREFS_NAME = "app_prefs"

        private const val KEY_UI_LANG = "ui_lang"
        private const val KEY_STUDY_LANG = "study_lang"
        private const val KEY_LEVELS = "levels"
        private const val KEY_DIFFICULTY = "difficulty"
    }
}

