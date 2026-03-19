package com.sleeplessdog.matchthewords

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.sleeplessdog.matchthewords.backend.data.repository.AppPrefs
import com.sleeplessdog.matchthewords.di.dataModule
import com.sleeplessdog.matchthewords.di.databaseModule
import com.sleeplessdog.matchthewords.di.domainModule
import com.sleeplessdog.matchthewords.di.presentationModule
import org.koin.android.ext.android.get
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin
import java.io.File

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

    private fun deleteAllDatabases(context: Context) {
        val dbDir = File(context.applicationInfo.dataDir, "databases")
        if (dbDir.exists() && dbDir.isDirectory) {
            dbDir.listFiles()?.forEach { dbFile ->
                if (dbFile.isFile) {
                    val deleted = dbFile.delete()
                    Log.d("DEBUG", "Удалена база: ${dbFile.name}, ok=$deleted")
                }
            }
        }
    }

    private fun setupCrashlytics() {
        val appPrefs: AppPrefs = get()
        FirebaseCrashlytics.getInstance().apply {
            setCustomKey("ui_language", appPrefs.getUiLanguage().name)
            setCustomKey("study_language", appPrefs.getStudyLanguage().name)
        }
    }

}