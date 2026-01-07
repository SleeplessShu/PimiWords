package com.sleeplessdog.matchthewords

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import com.sleeplessdog.matchthewords.di.dataModule
import com.sleeplessdog.matchthewords.di.domainModule
import com.sleeplessdog.matchthewords.di.presentationModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin
import java.io.File

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        val appContext = applicationContext
        deleteAllDatabases(appContext)

        startKoin {
            androidContext(this@App)
            modules(dataModule, domainModule, presentationModule)
        }
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

}