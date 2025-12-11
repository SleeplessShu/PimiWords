package com.sleeplessdog.matchthewords.utils

import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import com.sleeplessdog.matchthewords.R
import com.sleeplessdog.matchthewords.game.presentation.models.DifficultLevel
import com.sleeplessdog.matchthewords.game.presentation.models.Language
import com.sleeplessdog.matchthewords.utils.ConstantsApp.DATE_PATTERN
import com.sleeplessdog.matchthewords.utils.ConstantsApp.SCORE_FILL_CHAR
import com.sleeplessdog.matchthewords.utils.ConstantsApp.SCORE_LENGTH
import com.sleeplessdog.matchthewords.utils.ConstantsGamePrices.GAME_DIFFICULT_EASY
import com.sleeplessdog.matchthewords.utils.ConstantsGamePrices.GAME_DIFFICULT_HARD
import com.sleeplessdog.matchthewords.utils.ConstantsGamePrices.GAME_DIFFICULT_MEDIUM
import com.sleeplessdog.matchthewords.utils.ConstantsGamePrices.LIVES_EASY
import com.sleeplessdog.matchthewords.utils.ConstantsGamePrices.LIVES_EXPERT
import com.sleeplessdog.matchthewords.utils.ConstantsGamePrices.LIVES_HARD
import com.sleeplessdog.matchthewords.utils.ConstantsGamePrices.LIVES_MEDIUM
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

object SupportFunctions {

    fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat(DATE_PATTERN, Locale.getDefault())
        return dateFormat.format(Date())
    }

    fun getScoreAsString(score: Int): String {
        return score.toString().padStart(SCORE_LENGTH, SCORE_FILL_CHAR)
    }

    fun sortMapByDateDescending(inputMap: Map<String, Int>): Map<String, Int> {
        val dateFormatter = DateTimeFormatter.ofPattern(DATE_PATTERN)

        return inputMap.mapKeys { entry ->
            LocalDate.parse(
                entry.key, dateFormatter
            )
        }
            .toSortedMap(compareByDescending { it })
            .mapKeys { entry -> entry.key.format(dateFormatter) }
    }

    fun getGameDifficult(difficultLevel: DifficultLevel): Int {
        return when (difficultLevel) {
            DifficultLevel.EASY -> GAME_DIFFICULT_EASY
            DifficultLevel.MEDIUM -> GAME_DIFFICULT_MEDIUM
            DifficultLevel.HARD -> GAME_DIFFICULT_HARD
            DifficultLevel.EXPERT -> GAME_DIFFICULT_HARD
        }
    }

    fun getLivesCount(difficultLevel: DifficultLevel): Int {
        return when (difficultLevel) {
            DifficultLevel.EASY -> LIVES_EASY
            DifficultLevel.MEDIUM -> LIVES_MEDIUM
            DifficultLevel.HARD -> LIVES_HARD
            DifficultLevel.EXPERT -> LIVES_EXPERT
        }
    }

    fun Context.stringByName(name: String, uiLanguage: Language): String {
        val localized = withLanguage(uiLanguage)
        val resId = localized.resources.getIdentifier(
            name, "string", localized.packageName
        )

        return if (resId != 0) {
            localized.getString(resId)
        } else {
            name
        }
    }

    fun Context.drawableIdByName(name: String): Int {
        val id = resources.getIdentifier(name, "drawable", packageName)

        return if (id != 0) {
            id
        } else {
            R.drawable.ic_category_miscellaneous
        }
    }

    fun Context.withLanguage(lang: Language): Context {
        val locale = when (lang) {
            Language.RUSSIAN -> Locale("ru")
            Language.SPANISH -> Locale("es")
            Language.ENGLISH -> Locale("en")
            Language.FRENCH -> Locale("fr")
            Language.GERMAN -> Locale("de")
        }

        val config = Configuration(resources.configuration)
        config.setLocale(locale)
        return createConfigurationContext(config)
    }

    fun colorWithAlpha(color: Int, alpha: Float): Int {
        val a = (alpha.coerceIn(
            ConstantsApp.EMPTY_ALPHA, ConstantsApp.FULL_ALPHA
        ) * ConstantsApp.COLOR_MAX_CHANNEL).toInt()

        val r = Color.red(color)
        val g = Color.green(color)
        val b = Color.blue(color)

        return Color.argb(a, r, g, b)
    }
}
