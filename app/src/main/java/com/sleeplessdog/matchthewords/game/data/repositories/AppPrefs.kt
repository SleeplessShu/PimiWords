package com.sleeplessdog.matchthewords.game.data.repositories

import android.content.Context
import com.sleeplessdog.matchthewords.game.presentation.models.Language

interface AppPrefs {
    fun getUiLanguage(): Language
    fun getStudyLanguage(): Language
    fun save(ui: Language, study: Language)
}

class AppPrefsImpl(
    context: Context
) : AppPrefs {

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    override fun getUiLanguage(): Language {
        val name = prefs.getString(KEY_UI_LANG, Language.RUSSIAN.name)!!
        return Language.valueOf(name)
    }

    override fun getStudyLanguage(): Language {
        val name = prefs.getString(KEY_GAME_LANG, Language.ENGLISH.name)!!
        return Language.valueOf(name)
    }

    override fun save(ui: Language, study: Language) {
        prefs.edit()
            .putString(KEY_UI_LANG, ui.name)
            .putString(KEY_GAME_LANG, study.name)
            .apply()
    }

    companion object {
        private const val PREFS_NAME = "app_prefs"
        private const val KEY_UI_LANG = "ui_lang"
        private const val KEY_GAME_LANG = "game_lang"
    }
}
