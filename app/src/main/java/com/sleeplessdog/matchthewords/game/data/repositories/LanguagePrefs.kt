package com.sleeplessdog.matchthewords.game.data.repositories

import android.content.Context
import android.content.SharedPreferences
import com.sleeplessdog.matchthewords.game.presentation.models.Language
import com.sleeplessdog.matchthewords.utils.ConstantsPaths.KEY_STUDY_LANG
import com.sleeplessdog.matchthewords.utils.ConstantsPaths.KEY_UI_LANG
import com.sleeplessdog.matchthewords.utils.ConstantsPaths.PREFS_NAME
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

interface LanguagePrefs {

    fun observeUiLanguage(): Flow<Language>
    fun observeStudyLanguage(): Flow<Language>
    fun getUiLanguage(): Language
    fun getStudyLanguage(): Language
    fun saveLanguages(ui: Language, study: Language)
}

class LanguagePrefsImpl(
    context: Context
): LanguagePrefs {

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    // ---------- LANGS ----------
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
        val name = prefs.getString(KEY_UI_LANG, Language.RUSSIAN.name)
        return safeLang(name, Language.RUSSIAN)
    }

    override fun getStudyLanguage(): Language {
        val name = prefs.getString(KEY_STUDY_LANG, Language.ENGLISH.name)
        return safeLang(name, Language.ENGLISH)
    }

    override fun saveLanguages(ui: Language, study: Language) {
        prefs.edit().putString(KEY_UI_LANG, ui.name).putString(KEY_STUDY_LANG, study.name).apply()
    }

    private fun safeLang(value: String?, fallback: Language): Language = try {
        Language.valueOf(value ?: "")
    } catch (_: Exception) {
        fallback
    }
}
