package com.sleeplessdog.matchthewords.di

import android.os.Handler
import android.os.Looper
import com.sleeplessdog.matchthewords.game.data.repositories.AppPrefs
import com.sleeplessdog.matchthewords.game.data.repositories.AppPrefsImpl
import com.sleeplessdog.matchthewords.game.data.repositories.LanguagePrefs
import com.sleeplessdog.matchthewords.game.data.repositories.LanguagePrefsImpl
import com.sleeplessdog.matchthewords.game.domain.interactors.SettingsInteractor
import com.sleeplessdog.matchthewords.game.presentation.GameViewModel
import com.sleeplessdog.matchthewords.game.presentation.controller.GameLevelLoader
import com.sleeplessdog.matchthewords.game.presentation.controller.GameSessionController
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

    factory {
        GameLevelLoader(
            appPrefs = get(),
            languagePrefs = get(),
            wordsController = get(),
            getSelectedCategoriesUC = get()
        )
    }

    factory {
        GameSessionController(
            wordsController = get(),
            scoreInteractor = get(),
            progressController = get()
        )
    }

    viewModel {
        GameSelectViewModel(get())
    }

    viewModel {
        GameViewModel(
            levelLoader = get(), session = get()
        )
    }

    viewModel {
        ScoreViewModel(get())
    }

    viewModel {
        DatabaseViewModel(get(), get())
    }

    viewModel {
        OneOfFourViewModel(get())
    }

    viewModel {
        WriteTheWordViewModel()
    }

    viewModel {
        TrueOrFalseViewModel(get())
    }

    viewModel {
        WordsMatchingViewModel(get())
    }

    viewModel {
        SettingsViewModel(
            settingsInteractor = get(),
            app = androidApplication(),
            appPrefs = get(),
            languagePrefs = get()
        )
    }

    single<SettingsInteractor> {
        SettingsInteractor(get(), get(), get(), get(), get())
    }

    single<AppPrefs> { AppPrefsImpl(get()) }

    single<LanguagePrefs> { LanguagePrefsImpl(get()) }

    single { ShuffleFunctions() }

    single { ProgressController() }
}
