package com.sleeplessdog.matchthewords.game.data.repositories

import android.content.Context
import android.content.SharedPreferences
import com.sleeplessdog.matchthewords.game.domain.models.LanguageLevel
import com.sleeplessdog.matchthewords.game.presentation.models.DifficultLevel
import com.sleeplessdog.matchthewords.game.presentation.models.Language
import com.sleeplessdog.matchthewords.utils.ConstantsPaths.KEY_DIFFICULTY
import com.sleeplessdog.matchthewords.utils.ConstantsPaths.KEY_LEVELS
import com.sleeplessdog.matchthewords.utils.ConstantsPaths.KEY_STUDY_LANG
import com.sleeplessdog.matchthewords.utils.ConstantsPaths.KEY_UI_LANG
import com.sleeplessdog.matchthewords.utils.ConstantsPaths.PREFS_NAME
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

interface AppPrefs {



    fun getLevels(): Set<LanguageLevel>
    fun saveLevels(levels: Set<LanguageLevel>)

    fun getDifficulty(): DifficultLevel
    fun saveDifficulty(level: DifficultLevel)
}

class AppPrefsImpl(
    context: Context
) : AppPrefs {

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

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
}
