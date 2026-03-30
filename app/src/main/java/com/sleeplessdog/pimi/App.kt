package com.sleeplessdog.pimi

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.sleeplessdog.pimi.di.dataModule
import com.sleeplessdog.pimi.di.databaseModule
import com.sleeplessdog.pimi.di.domainModule
import com.sleeplessdog.pimi.di.presentationModule
import com.sleeplessdog.pimi.games.data.repository.AppPrefs
import org.koin.android.ext.android.get
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)

        startKoin {
            androidContext(this@App)
            modules(databaseModule, dataModule, domainModule, presentationModule)
        }

        setupCrashlytics()

        FirebaseAppCheck.getInstance().installAppCheckProviderFactory(
            DebugAppCheckProviderFactory.getInstance()
        )
    }

    private fun setupCrashlytics() {
        val appPrefs: AppPrefs = get()
        FirebaseCrashlytics.getInstance().apply {
            setCustomKey("ui_language", appPrefs.getUiLanguage().name)
            setCustomKey("study_language", appPrefs.getStudyLanguage().name)
        }
    }
}
