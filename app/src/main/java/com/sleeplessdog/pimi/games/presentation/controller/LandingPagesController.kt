package com.sleeplessdog.pimi.games.presentation.controller

import android.content.Context
import androidx.core.content.edit
import com.sleeplessdog.pimi.gameSelect.LandingKeys
import com.sleeplessdog.pimi.utils.ConstantsPaths.LANDING_SCREENS_PREFS

/***
 * Класс для управления показом экранов приветствия
 */
class LandingPagesController(context: Context) {
    private val prefs = context.getSharedPreferences(LANDING_SCREENS_PREFS, Context.MODE_PRIVATE)


    fun shouldShow(key: LandingKeys): Boolean {
        return !prefs.getBoolean(key.name, false)
    }

    fun setShown(key: LandingKeys) {
        prefs.edit { putBoolean(key.name, true) }
    }
}
