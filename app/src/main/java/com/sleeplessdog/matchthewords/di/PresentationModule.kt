package com.sleeplessdog.matchthewords.di

import android.os.Handler
import android.os.Looper
import com.sleeplessdog.matchthewords.game.data.repositories.AppPrefs
import com.sleeplessdog.matchthewords.game.data.repositories.AppPrefsImpl
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
import com.sleeplessdog.matchthewords.settings.presentation.DatabaseViewModel
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
        )
    }

    viewModel {
        GameViewModel(
            wordsController = get(),
            progressController = get(),
            scoreInteractor = get(),
            appPrefs = get(),
            getSelectedCategoriesUC = get(),
            landingManager = get(),
        )
    }

    viewModel {
        ScoreViewModel(get())
    }

    viewModel() {
        DatabaseViewModel(get(), get())
    }

    viewModel() {
        OneOfFourViewModel(get())
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
        EndGameViewModel(get())
    }

    viewModel {
        SettingsViewModel(
            observeFeaturedUC = get(),
            observeAllGroupedUC = get(),
            toggleUC = get(),
            saveSelectionUC = get(),
            createUserUC = get(),
            app = androidApplication(),
            appPrefs = get()
        )
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
}
