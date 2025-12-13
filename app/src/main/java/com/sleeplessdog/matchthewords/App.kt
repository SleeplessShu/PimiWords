package com.sleeplessdog.matchthewords

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import com.sleeplessdog.matchthewords.di.dataModule
import com.sleeplessdog.matchthewords.di.domainModule
import com.sleeplessdog.matchthewords.di.presentationModule
import com.sleeplessdog.matchthewords.game.data.database.AppDatabase
import com.sleeplessdog.matchthewords.utils.ConstantsPaths.CRASH_FILE
import com.sleeplessdog.matchthewords.utils.ConstantsPaths.KEY_HAS_CRASH
import com.sleeplessdog.matchthewords.utils.ConstantsPaths.PREFS
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin
import java.io.File
import java.io.IOException
import java.io.PrintWriter
import java.io.StringWriter
import java.text.SimpleDateFormat
import java.util.Locale

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        appContext = applicationContext
        deleteAllDatabases(appContext)

        setCrashHandler()

        startKoin {
            androidContext(this@App)
            modules(dataModule, domainModule, presentationModule)
        }
        FirebaseAppCheck.getInstance().installAppCheckProviderFactory(
            DebugAppCheckProviderFactory.getInstance(),
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

    private fun setCrashHandler() {
        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            try {
                val sw = StringWriter()
                throwable.printStackTrace(PrintWriter(sw))
                val stackTrace = sw.toString()

                val ts = SimpleDateFormat(
                    "yyyy-MM-dd HH:mm:ss",
                    Locale.US,
                ).format(
                    System.currentTimeMillis(),
                )
                val header = buildString {
                    appendLine("=== Crash ===")
                    appendLine("Time      : $ts")
                    appendLine("Thread    : ${thread.name} (${thread.id})")
                    appendLine("Device    : ${android.os.Build.MANUFACTURER} ${android.os.Build.MODEL}")
                    appendLine("SDK       : ${android.os.Build.VERSION.SDK_INT}")
                    appendLine("------------------------------")
                }

                openFileOutput(CRASH_FILE, MODE_APPEND).use { fos ->
                    fos.write((header + stackTrace + "\n\n").toByteArray())
                }

                getSharedPreferences(PREFS, MODE_PRIVATE).edit {
                    putBoolean(KEY_HAS_CRASH, true)
                }

                Log.e("CRASH", stackTrace)
            } catch (e: IOException) {
                Log.e("CRASH", "Failed to save crash", e)
            } finally {

                defaultHandler?.uncaughtException(thread, throwable) ?: run {
                    android.os.Process.killProcess(android.os.Process.myPid())
                }
            }
        }
    }

    companion object {
        lateinit var database: AppDatabase
        lateinit var appContext: Context

        fun crashFilePath(): String = appContext.filesDir.resolve(CRASH_FILE).absolutePath

        fun clearCrashFlag() {
            appContext.getSharedPreferences(PREFS, MODE_PRIVATE).edit {
                putBoolean(KEY_HAS_CRASH, false).apply()
            }
        }

        fun hasCrash(): Boolean =
            appContext
                .getSharedPreferences(PREFS, MODE_PRIVATE)
                .getBoolean(KEY_HAS_CRASH, false)
    }
}
