package com.sleeplessdog.pimi

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
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

class App : Application(), DefaultLifecycleObserver {

    override fun onCreate() {
        super<Application>.onCreate()

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)

        startKoin {
            androidContext(this@App)
            modules(databaseModule, dataModule, domainModule, presentationModule)
        }

        setupCrashlytics()

        FirebaseAppCheck.getInstance().installAppCheckProviderFactory(
            DebugAppCheckProviderFactory.getInstance()
        )

        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    override fun onStop(owner: LifecycleOwner) {
        /*val syncController: DatabaseSyncController = get()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                syncController.checkUserDatabase()
            } catch (e: Exception) {
            }
        }*/
    }

    private fun setupCrashlytics() {
        val appPrefs: AppPrefs = get()
        FirebaseCrashlytics.getInstance().apply {
            setCustomKey("ui_language", appPrefs.getUiLanguage().name)
            setCustomKey("study_language", appPrefs.getStudyLanguage().name)
        }
    }
}