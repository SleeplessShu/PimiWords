package com.sleeplessdog.matchthewords

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
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
        //seedWordPacks()
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

    private fun seedWordPacks() {
        val db = Firebase.firestore
        val pack = hashMapOf(
            "name" to "Вождение",
            "languagePair" to "russian_spanish",
            "wordsCount" to 20,
            "words" to listOf(
                mapOf("russian" to "руль", "spanish" to "volante"),
                mapOf("russian" to "тормоз", "spanish" to "freno"),
                mapOf("russian" to "газ", "spanish" to "acelerador"),
                mapOf("russian" to "сцепление", "spanish" to "embrague"),
                mapOf("russian" to "коробка передач", "spanish" to "caja de cambios"),
                mapOf("russian" to "зеркало заднего вида", "spanish" to "espejo retrovisor"),
                mapOf("russian" to "ремень безопасности", "spanish" to "cinturón de seguridad"),
                mapOf("russian" to "поворотник", "spanish" to "intermitente"),
                mapOf("russian" to "фары", "spanish" to "faros"),
                mapOf("russian" to "дворники", "spanish" to "limpiaparabrisas"),
                mapOf("russian" to "капот", "spanish" to "capó"),
                mapOf("russian" to "багажник", "spanish" to "maletero"),
                mapOf("russian" to "шина", "spanish" to "neumático"),
                mapOf("russian" to "бензин", "spanish" to "gasolina"),
                mapOf("russian" to "парковка", "spanish" to "aparcamiento"),
                mapOf("russian" to "светофор", "spanish" to "semáforo"),
                mapOf("russian" to "перекрёсток", "spanish" to "cruce"),
                mapOf("russian" to "обгон", "spanish" to "adelantamiento"),
                mapOf("russian" to "штраф", "spanish" to "multa"),
                mapOf("russian" to "водительские права", "spanish" to "carnet de conducir"),
            )
        )
        db.collection("word_packs").add(pack)
    }

}