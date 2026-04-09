package com.sleeplessdog.pimi.di

import android.os.Handler
import android.os.Looper
import com.sleeplessdog.pimi.dictionary.GroupDictionaryUiMapper
import com.sleeplessdog.pimi.dictionary.dictionary_screen.DatabaseSyncController
import com.sleeplessdog.pimi.dictionary.dictionary_screen.DictionaryViewModel
import com.sleeplessdog.pimi.dictionary.dictionary_screen.FirebaseAuthController
import com.sleeplessdog.pimi.dictionary.group_screen.GroupViewModel
import com.sleeplessdog.pimi.dictionary.models.GroupSettingsUiMapper
import com.sleeplessdog.pimi.dictionary.word_packs.WordPacksViewModel
import com.sleeplessdog.pimi.endGame.EndGameViewModel
import com.sleeplessdog.pimi.gameSelect.GameSelectViewModel
import com.sleeplessdog.pimi.games.data.repository.AppPrefs
import com.sleeplessdog.pimi.games.data.repository.AppPrefsImpl
import com.sleeplessdog.pimi.games.presentation.GameViewModel
import com.sleeplessdog.pimi.games.presentation.controller.LandingPagesController
import com.sleeplessdog.pimi.games.presentation.ingameFragments.OneOfFourViewModel
import com.sleeplessdog.pimi.games.presentation.ingameFragments.TrueOrFalseViewModel
import com.sleeplessdog.pimi.games.presentation.ingameFragments.WordsMatchingViewModel
import com.sleeplessdog.pimi.games.presentation.ingameFragments.WriteTheWordViewModel
import com.sleeplessdog.pimi.games.presentation.parentControllers.ProgressController
import com.sleeplessdog.pimi.score.presentation.ScoreViewModel
import com.sleeplessdog.pimi.settings.SettingsViewModel
import com.sleeplessdog.pimi.utils.ShuffleFunctions
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val presentationModule = module {

    factory<Handler> {
        Handler(Looper.getMainLooper())
    }

    viewModel {
        GameSelectViewModel(
            appPrefs = get(),
            landingManager = get(),
            observeAllGroupsForSettings = get(),
            groupSettingsUiMapper = get(),
            app = get(),
        )
    }

    viewModel {
        GameViewModel(

            wordsController = get(),
            progressController = get(),
            landingManager = get(),
            getSelectedGroupsUC = get(),
            appPrefs = get(),
            getWordPairsFromUserGroupUC = get(),
            processGameResultUC = get(),
        )
    }

    viewModel {
        ScoreViewModel(
            getScoreUiState = get()
        )
    }
    viewModel {
        GroupViewModel(
            observeUserGroupsForGroups = get(),
            observeWordsInUserGroup = get(),
            getGlobalGroupWordsOnce = get(),
            savedStateHandle = get(),
            addWordToUserGroup = get(),
            editWordInUserGroup = get(),
            deleteWordFromUserGroup = get(),
            moveWordToUserGroup = get(),
            appPrefs = get(),
            addSingleWordToSavedWords = get(),
            premiumGate = get(),
            app = get(),
        )
    }
    viewModel {
        DictionaryViewModel(
            observeAllGroups = get(),
            createUserGroup = get(),
            groupDictionaryUiMapper = get(),
            renameUserGroup = get(),
            deleteUserGroup = get(),
            syncController = get(),
            authController = get(),
            premiumGate = get(),
        )
    }

    viewModel() {
        OneOfFourViewModel(get())
    }
    viewModel() {
        WordPacksViewModel(get(), get(), get())
    }

    viewModel() {
        WriteTheWordViewModel()
    }

    viewModel {
        TrueOrFalseViewModel(get())
    }

    viewModel {
        WordsMatchingViewModel(get())
    }

    viewModel {
        EndGameViewModel(get(), get())
    }

    viewModel {
        val settingsViewModel = SettingsViewModel(
            observeAllGroups = get(),
            toggleUC = get(),
            saveSelectionUC = get(),
            saveLevelsUC = get(),
            observeLevelsUC = get(),
            app = androidApplication(),
            appPrefs = get(),
            groupSettingsUiMapper = get()
        )
        settingsViewModel
    }
    single { DatabaseSyncController(get(), get(), get()) }
    single<AppPrefs> {
        AppPrefsImpl(
            context = get(),
        )
    }

    single {
        LandingPagesController(
            context = get(),
        )
    }

    single { ShuffleFunctions() }
    single { ProgressController() }
    single { GroupSettingsUiMapper(get()) }
    single { GroupDictionaryUiMapper(get()) }

    single { FirebaseAuthController(get()) }
}
