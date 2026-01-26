package com.sleeplessdog.matchthewords.utils


import android.content.Context
import android.content.res.Configuration
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import com.google.android.material.chip.Chip
import com.sleeplessdog.matchthewords.R
import com.sleeplessdog.matchthewords.game.presentation.models.DifficultLevel
import com.sleeplessdog.matchthewords.game.presentation.models.GameType
import com.sleeplessdog.matchthewords.game.presentation.models.GroupUiSettings
import com.sleeplessdog.matchthewords.game.presentation.models.LandingKeys
import com.sleeplessdog.matchthewords.game.presentation.models.Language
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

object SupportFunctions {

    fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(Date())
    }

    fun getScoreAsString(score: Int): String {
        return score.toString().padStart(9, '0')
    }

    fun sortMapByDateDescending(inputMap: Map<String, Int>): Map<String, Int> {
        val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

        return inputMap.mapKeys { entry ->
            LocalDate.parse(
                entry.key, dateFormatter
            )
        } // Преобразуем ключи в LocalDate
            .toSortedMap(compareByDescending { it }) // Сортируем по убыванию дат
            .mapKeys { entry -> entry.key.format(dateFormatter) } // Преобразуем обратно ключи в строковый формат
    }

    fun getGameDifficult(difficultLevel: DifficultLevel): Int {
        return when (difficultLevel) {
            DifficultLevel.EASY -> 12
            DifficultLevel.MEDIUM -> 24
            DifficultLevel.HARD -> 48
            DifficultLevel.EXPERT -> 48
        }
    }

    fun getLivesCount(difficultLevel: DifficultLevel): Int {
        return when (difficultLevel) {
            DifficultLevel.EASY -> 3
            DifficultLevel.MEDIUM -> 3
            DifficultLevel.HARD -> 2
            DifficultLevel.EXPERT -> 1
        }
    }

    fun getKeyByGameType(gameType: GameType): LandingKeys {
        return when (gameType) {
            GameType.MATCH8 -> LandingKeys.GAME_MTW
            GameType.TRUEorFALSE -> LandingKeys.GAME_TOF
            GameType.OneOfFour -> LandingKeys.GAME_OOF
            GameType.WriteTheWord -> LandingKeys.GAME_WTW
        }
    }

    fun Context.stringByName(name: String, uiLanguage: Language): String {
        val localized = withLanguage(uiLanguage)
        val resId = localized.resources.getIdentifier(name, "string", packageName)
        return if (resId != 0) localized.getString(resId) else name
    }


    fun Context.drawableIdByName(name: String): Int {
        val id = resources.getIdentifier(name, "drawable", packageName)
        Log.e("DRAWABLE_RES", "Drawable not found for name = $name")
        return if (id != 0) id else R.drawable.ic_category_miscellaneous
    }

    fun createCategoryChip(parent: ViewGroup, item: GroupUiSettings): Chip {
        val ctx = parent.context
        val chip =
            LayoutInflater.from(ctx).inflate(R.layout.view_category_chip, parent, false) as Chip

        chip.text = item.title
        chip.isCheckable = true
        chip.tag = item.key
        chip.chipBackgroundColor = ContextCompat.getColorStateList(
            ctx, R.color.selector_options_button_bg
        )
        if (item.iconRes != 0) {
            chip.chipIcon = AppCompatResources.getDrawable(ctx, item.iconRes)
            chip.isChipIconVisible = true
        } else {
            chip.isChipIconVisible = false
        }

        return chip
    }

    fun Context.withLanguage(lang: Language): Context {
        val locale = when (lang) {
            Language.RUSSIAN -> Locale("ru")
            Language.SPANISH -> Locale("es")
            Language.ENGLISH -> Locale("en")
            Language.FRENCH -> Locale("fr")
            Language.GERMAN -> Locale("ge")
            Language.ARMENIAN -> Locale("hy")
            Language.SERBIAN -> Locale("sr")
        }

        val config = Configuration(resources.configuration)
        config.setLocale(locale)
        return createConfigurationContext(config)
    }
}