package com.sleeplessdog.matchthewords.game.presentation.controller

import com.sleeplessdog.matchthewords.game.data.repositories.AppPrefs
import com.sleeplessdog.matchthewords.game.data.repositories.LanguagePrefs
import com.sleeplessdog.matchthewords.game.domain.interactors.WordsController
import com.sleeplessdog.matchthewords.game.domain.models.WordsCategoriesList
import com.sleeplessdog.matchthewords.game.domain.usecase.GetSelectedCategoriesUC
import com.sleeplessdog.matchthewords.game.presentation.models.GameSettings
import com.sleeplessdog.matchthewords.game.presentation.models.GameType
import com.sleeplessdog.matchthewords.game.presentation.models.Word
import com.sleeplessdog.matchthewords.utils.ConstantsGamePrices.ONE_OF_FOUR_MULTIPLIER
import com.sleeplessdog.matchthewords.utils.ConstantsGamePrices.WRITE_WORD_DIVIDER_LIST
import com.sleeplessdog.matchthewords.utils.SupportFunctions

class GameLevelLoader(
    private val appPrefs: AppPrefs,
    private val languagePrefs: LanguagePrefs,
    private val wordsController: WordsController,
    private val getSelectedCategoriesUC: GetSelectedCategoriesUC
) {

    data class LevelData(
        val settings: GameSettings,
        val words: List<Pair<Word, Word>>,
        val difficultyValue: Int
    )

    suspend fun loadLevel(gameType: GameType): LevelData? {
        val selectedCategories = getSelectedCategoriesUC()
        val enums = selectedCategories.mapNotNull { cat ->
            WordsCategoriesList.entries.find { it.key == cat.key }
        }.toSet()

        val settings = GameSettings(
            language1 = languagePrefs.getUiLanguage(),
            language2 = languagePrefs.getStudyLanguage(),
            difficult = appPrefs.getDifficulty(),
            level = appPrefs.getLevels(),
            category = enums
        )

        val diffValue = SupportFunctions.getGameDifficult(settings.difficult)

        val wordsNeeded = when (gameType) {
            GameType.WriteTheWord -> diffValue / WRITE_WORD_DIVIDER_LIST
            GameType.OneOfFour -> diffValue * ONE_OF_FOUR_MULTIPLIER
            else -> diffValue
        }

        val pairs = wordsController.getWordPairs(
            settings.language1,
            settings.language2,
            settings.level,
            wordsNeeded,
            settings.category
        )

        if (pairs.isEmpty()) return null

        return LevelData(settings, pairs, diffValue)
    }
}
