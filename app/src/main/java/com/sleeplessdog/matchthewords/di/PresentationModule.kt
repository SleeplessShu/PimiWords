package com.sleeplessdog.matchthewords.di

import android.os.Handler
import android.os.Looper
import com.sleeplessdog.matchthewords.backend.data.repository.AppPrefs
import com.sleeplessdog.matchthewords.backend.data.repository.AppPrefsImpl
import com.sleeplessdog.matchthewords.dictionary.GroupDictionaryUiMapper
import com.sleeplessdog.matchthewords.dictionary.dictionary_screen.DatabaseSyncController
import com.sleeplessdog.matchthewords.dictionary.dictionary_screen.DictionaryViewModel
import com.sleeplessdog.matchthewords.dictionary.dictionary_screen.FirebaseAuthController
import com.sleeplessdog.matchthewords.dictionary.group_screen.GroupViewModel
import com.sleeplessdog.matchthewords.dictionary.models.GroupSettingsUiMapper
import com.sleeplessdog.matchthewords.dictionary.word_packs.WordPacksViewModel
import com.sleeplessdog.matchthewords.game.presentation.GameViewModel
import com.sleeplessdog.matchthewords.game.presentation.controller.LandingPagesController
import com.sleeplessdog.matchthewords.game.presentation.fragments.EndGameViewModel
import com.sleeplessdog.matchthewords.game.presentation.fragments.GameSelectViewModel
import com.sleeplessdog.matchthewords.game.presentation.fragments.SettingsViewModel
import com.sleeplessdog.matchthewords.game.presentation.ingameFragments.OneOfFourViewModel
import com.sleeplessdog.matchthewords.game.presentation.ingameFragments.TrueOrFalseViewModel
import com.sleeplessdog.matchthewords.game.presentation.ingameFragments.WordsMatchingViewModel
import com.sleeplessdog.matchthewords.game.presentation.ingameFragments.WriteTheWordViewModel
import com.sleeplessdog.matchthewords.game.presentation.parentControllers.ProgressController
import com.sleeplessdog.matchthewords.score.presentation.ScoreViewModel
import com.sleeplessdog.matchthewords.utils.ShuffleFunctions
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
            createUserGroupUC = get(),
            saveLevelsUC = get(),
            observeLevelsUC = get(),
            app = androidApplication(),
            appPrefs = get(),
            groupSettingsUiMapper = get()
        )
        settingsViewModel
    }

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
    single { DatabaseSyncController(get(), get()) }
    single { FirebaseAuthController(get()) }
}
