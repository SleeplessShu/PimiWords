package com.sleeplessdog.matchthewords.di

import android.os.Handler
import android.os.Looper
import com.sleeplessdog.matchthewords.game.data.repositories.AppPrefs
import com.sleeplessdog.matchthewords.game.data.repositories.AppPrefsImpl
import com.sleeplessdog.matchthewords.game.domain.api.ScoreInteractor
import com.sleeplessdog.matchthewords.game.presentation.GameViewModel
import com.sleeplessdog.matchthewords.game.presentation.ingameFragments.OneOfFourViewModel
import com.sleeplessdog.matchthewords.game.presentation.ingameFragments.TrueOrFalseViewModel
import com.sleeplessdog.matchthewords.game.presentation.ingameFragments.WordsMatchingViewModel
import com.sleeplessdog.matchthewords.game.presentation.ingameFragments.WriteTheWordViewModel
import com.sleeplessdog.matchthewords.game.presentation.parentControllers.ProgressController
import com.sleeplessdog.matchthewords.gameSelect.presentation.GameSelectViewModel
import com.sleeplessdog.matchthewords.score.presentation.ScoreViewModel
import com.sleeplessdog.matchthewords.settings.presentation.DatabaseViewModel
import com.sleeplessdog.matchthewords.settings.presentation.SettingsViewModel
import com.sleeplessdog.matchthewords.utils.ShuffleFunctions
import com.sleeplessdog.matchthewords.utils.SupportFunctions
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val presentationModule = module {

    factory<Handler> {
        Handler(Looper.getMainLooper())
    }
    viewModel {
        SettingsViewModel(get(), get())
    }

    viewModel {
        GameSelectViewModel( get() )
    }

    viewModel {
        GameViewModel( get(), get(), get(), get() )
    }

    viewModel {
        ScoreViewModel(get(), get())
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

    single<AppPrefs> { AppPrefsImpl(get()) }

    single { SupportFunctions() }

    single { ShuffleFunctions() }

    single { ProgressController( get() ) }
}