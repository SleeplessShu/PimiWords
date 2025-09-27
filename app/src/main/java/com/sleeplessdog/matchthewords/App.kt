package com.sleeplessdog.matchthewords

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.room.Room
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import com.sleeplessdog.matchthewords.di.domainModule
import com.sleeplessdog.matchthewords.di.presentationModule
import com.sleeplessdog.matchthewords.game.data.database.AppDatabase
import com.sleeplessdog.matchthewords.settings.domain.api.SettingsInteractor

import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin
import org.koin.java.KoinJavaComponent.getKoin
import java.io.File


class App : Application() {
    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
        startKoin {
            androidContext(this@App)
            modules(dataModule, domainModule, presentationModule)
        }
        FirebaseAppCheck.getInstance().installAppCheckProviderFactory(
            DebugAppCheckProviderFactory.getInstance()
        )
        val settingsInteractor: SettingsInteractor = getKoin().get()
        val isNightModeOn = settingsInteractor.getThemeSettings()
        AppCompatDelegate.setDefaultNightMode(
            if (isNightModeOn) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )

    }

    private fun deleteExistingDatabase(context: Context, dbName: String) {
        val dbPath = context.getDatabasePath(dbName)
        if (dbPath.exists()) {
            dbPath.delete()
            Log.d("DEBUG", "Старая база данных удалена.")
        }
    }

    private fun databaseSelector() {
        val dbsDir = applicationContext.getDatabasePath("stub").parentFile!!
        dbsDir.mkdirs()

        val filesDb = File(applicationContext.filesDir, "latest_db.db")
        if (filesDb.exists()) {
            val target = applicationContext.getDatabasePath("latest_db.db")
            // всегда обновляем, если файлы отличаются (или просто overwrite = true)
            filesDb.copyTo(target, overwrite = true)
            database = Room.databaseBuilder(appContext, AppDatabase::class.java, "latest_db.db")
                .build()
            Log.d("DEBUG", "DB -> latest_db.db (copied from filesDir)")
        } else {
            // не удаляем каждую загрузку! убери вызов deleteExistingDatabase
            database = Room.databaseBuilder(appContext, AppDatabase::class.java, "dictionary.db")
                .createFromAsset("databases/dictionary_default.db")
                .build()
        }
    }


    companion object {

        lateinit var database: AppDatabase
        lateinit var appContext: Context
            private set
    }
}