package com.sleeplessdog.matchthewords.di

import android.content.Context
import android.content.SharedPreferences
import android.os.Handler
import androidx.core.content.edit
import androidx.room.Room
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.sleeplessdog.matchthewords.App
import com.sleeplessdog.matchthewords.game.data.database.AppDatabase
import com.sleeplessdog.matchthewords.game.data.database.WordCategoryDao
import com.sleeplessdog.matchthewords.game.data.database.resolveAssetDatabase
import com.sleeplessdog.matchthewords.game.data.repositories.ScoreRepositoryImpl
import com.sleeplessdog.matchthewords.game.data.repositories.WordCategoriesRepositoryImpl
import com.sleeplessdog.matchthewords.game.data.repositories.WordsDatabase
import com.sleeplessdog.matchthewords.game.domain.repositories.ScoreRepository
import com.sleeplessdog.matchthewords.game.domain.repositories.WordCategoriesRepository
import com.sleeplessdog.matchthewords.game.domain.usecase.CreateUserCategoryUC
import com.sleeplessdog.matchthewords.game.domain.usecase.DeleteUserCategoryUC
import com.sleeplessdog.matchthewords.game.domain.usecase.GetSelectedCategoriesUC
import com.sleeplessdog.matchthewords.game.domain.usecase.ObserveAllCategoriesGroupedUC
import com.sleeplessdog.matchthewords.game.domain.usecase.ObserveFeaturedCategoriesUC
import com.sleeplessdog.matchthewords.game.domain.usecase.SaveSelectionUC
import com.sleeplessdog.matchthewords.game.domain.usecase.ToggleCategoryUC
import com.sleeplessdog.matchthewords.server.data.ServerDateRepositoryImpl
import com.sleeplessdog.matchthewords.server.domain.ServerDateRepository
import com.sleeplessdog.matchthewords.server.domain.ServerDbInteractor
import com.sleeplessdog.matchthewords.server.domain.ServerDbInteractorImpl
import com.sleeplessdog.matchthewords.utils.AppDb
import com.sleeplessdog.matchthewords.utils.ConstantsPaths.DICTIONARY_NAME
import com.sleeplessdog.matchthewords.utils.ConstantsPaths.FIREBASE_PATH
import com.sleeplessdog.matchthewords.utils.ConstantsPaths.KEY_DB_DATE
import com.sleeplessdog.matchthewords.utils.ConstantsPaths.KEY_DB_PREFS
import com.sleeplessdog.matchthewords.utils.ConstantsPaths.KEY_DB_SCORE
import com.sleeplessdog.matchthewords.utils.ConstantsPaths.KEY_DB_SCORE_HISTORY
import com.sleeplessdog.matchthewords.utils.ConstantsPaths.NIGHT_MODE
import com.sleeplessdog.matchthewords.utils.ConstantsPaths.THEME_PREFERENCES
import org.koin.core.qualifier.named
import org.koin.dsl.module

val dataModule =
    module {

    single {
        FirebaseDatabase.getInstance(FIREBASE_PATH)
    }

    single {
        FirebaseStorage.getInstance()
    }

    single(named(THEME_PREFERENCES)) {
        App.appContext.getSharedPreferences(NIGHT_MODE, Context.MODE_PRIVATE)
    }

    single<WordsDatabase> {
        WordsDatabase(get())
    }

    factory<Handler> {
        Handler()
    }

    single<Context> {
        App.appContext
    }

    single { get<AppDatabase>().wordDao() }

    single(named(KEY_DB_PREFS)) {
        App.appContext.getSharedPreferences(KEY_DB_PREFS, Context.MODE_PRIVATE)
    }

    single {
        val dbName = DICTIONARY_NAME
        val ctx: Context = get()

        val sel = resolveAssetDatabase(ctx) // получаем и путь, и дату

        val prefs: SharedPreferences = get(qualifier = named(KEY_DB_PREFS))
        prefs.edit { putString(KEY_DB_DATE, sel.date) }

        Room.databaseBuilder(ctx, AppDatabase::class.java, dbName).createFromAsset(sel.assetPath)
            .build()
    }

    single(named(KEY_DB_SCORE)) {
        App.appContext.getSharedPreferences(KEY_DB_SCORE_HISTORY, Context.MODE_PRIVATE)
    }
    single<ScoreRepository> {
        ScoreRepositoryImpl(get(named(KEY_DB_SCORE)))
    }

    single<ServerDateRepository> {
        ServerDateRepositoryImpl(get(named(KEY_DB_PREFS)))
    }

    single<ServerDbInteractor> {
        ServerDbInteractorImpl(get(), get())
    }

    single { AppDb.build(get()) }
    single<WordCategoryDao> {
        get<AppDb>().wordCategoryDao()
    }

    single<WordCategoriesRepository> { WordCategoriesRepositoryImpl(get()) }
    factory { ObserveFeaturedCategoriesUC(get()) }
    factory { ObserveAllCategoriesGroupedUC(get()) }
    factory { ToggleCategoryUC(get()) }
    factory { SaveSelectionUC(get()) }
    factory { CreateUserCategoryUC(get()) }
    factory { DeleteUserCategoryUC(get()) }
    factory { GetSelectedCategoriesUC(get()) }
}
