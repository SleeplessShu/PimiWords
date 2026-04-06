package com.sleeplessdog.pimi.utils


import android.content.Context
import android.content.res.Configuration
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import com.google.android.material.chip.Chip
import com.sleeplessdog.pimi.R
import com.sleeplessdog.pimi.gameSelect.LandingKeys
import com.sleeplessdog.pimi.games.domain.models.GroupUiSettings
import com.sleeplessdog.pimi.games.presentation.models.GameType
import com.sleeplessdog.pimi.settings.DifficultyLevel
import com.sleeplessdog.pimi.settings.Language
import java.util.Locale

object SupportFunctions {

    fun getGameDifficult(difficultyLevel: DifficultyLevel): Int {
        return when (difficultyLevel) {
            DifficultyLevel.EASY -> 12
            DifficultyLevel.MEDIUM -> 24
            DifficultyLevel.HARD -> 48
            DifficultyLevel.EXPERT -> 48
        }
    }

    fun getLivesCount(difficultyLevel: DifficultyLevel): Int {
        return when (difficultyLevel) {
            DifficultyLevel.EASY -> 3
            DifficultyLevel.MEDIUM -> 3
            DifficultyLevel.HARD -> 2
            DifficultyLevel.EXPERT -> 1
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
        return if (id != 0) id else R.drawable.ic_group_miscellaneous
    }

    fun createCategoryChip(parent: ViewGroup, item: GroupUiSettings): Chip {
        val context = parent.context
        val chip =
            LayoutInflater.from(context).inflate(R.layout.view_category_chip, parent, false) as Chip

        chip.text = item.title ?: getGroupUiName(context, item.titleRes, item.key)
        chip.isCheckable = true
        chip.tag = item.key
        chip.chipBackgroundColor = ContextCompat.getColorStateList(
            context, R.color.selector_options_button_bg
        )
        if (item.iconRes != 0) {
            chip.chipIcon = AppCompatResources.getDrawable(context, item.iconRes)
            chip.isChipIconVisible = true
        } else {
            chip.isChipIconVisible = false
        }

        return chip
    }

    fun getGroupUiName(context: Context, titleRes: Int, key: String): String {
        return if (titleRes != 0) {
            context.getString(titleRes)
        } else {
            key
        }
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