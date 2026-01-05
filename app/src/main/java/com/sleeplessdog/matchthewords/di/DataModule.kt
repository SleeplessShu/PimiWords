package com.sleeplessdog.matchthewords.di

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.room.Room
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.sleeplessdog.matchthewords.App
import com.sleeplessdog.matchthewords.game.data.database.AppDictionaryDatabase
import com.sleeplessdog.matchthewords.game.data.database.AppGroupsDictionary
import com.sleeplessdog.matchthewords.game.data.database.UserDictionaryDatabase
import com.sleeplessdog.matchthewords.game.data.database.WordCategoryDao
import com.sleeplessdog.matchthewords.game.data.repositories.ScoreRepositoryImpl
import com.sleeplessdog.matchthewords.game.data.repositories.UserDictionaryRepository
import com.sleeplessdog.matchthewords.game.data.repositories.WordCategoriesRepositoryImpl
import com.sleeplessdog.matchthewords.game.data.repositories.WordsDatabase
import com.sleeplessdog.matchthewords.game.domain.repositories.ScoreRepository
import com.sleeplessdog.matchthewords.game.domain.repositories.WordCategoriesRepository
import com.sleeplessdog.matchthewords.game.domain.usecase.AddWordToUserDictionaryUC
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
import com.sleeplessdog.matchthewords.utils.ConstantsPaths.FIREBASE_KEY
import com.sleeplessdog.matchthewords.utils.ConstantsPaths.LOCAL_DATABASE_DICTIONARY_NAME
import com.sleeplessdog.matchthewords.utils.ConstantsPaths.SHARED_PREFS_DATABASE_SETTINGS
import com.sleeplessdog.matchthewords.utils.ConstantsPaths.SHARED_PREFS_SCORE_KEY
import com.sleeplessdog.matchthewords.utils.ConstantsPaths.SHARED_PREFS_SCORE_REPOSITORY
import com.sleeplessdog.matchthewords.utils.ConstantsPaths.SHARED_PREFS_THEME_KEY
import com.sleeplessdog.matchthewords.utils.ConstantsPaths.SHARED_PREFS_THEME_REPOSITORY
import com.sleeplessdog.matchthewords.utils.ConstantsPaths.USER_DATABASE_DICTIONARY_NAME
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module

val dataModule = module {

    single {
        FirebaseDatabase.getInstance(FIREBASE_KEY)
    }

    single {
        FirebaseStorage.getInstance()
    }

    single(named(SHARED_PREFS_THEME_KEY)) {
        App.appContext.getSharedPreferences(SHARED_PREFS_THEME_REPOSITORY, Context.MODE_PRIVATE)
    }

    single<WordsDatabase> {
        WordsDatabase(get())
    }
    factory<Handler> {
        Handler(Looper.getMainLooper())
    }
    single<Context> {
        App.appContext
    }
    single { get<AppDictionaryDatabase>().wordDao() }

    single(named(SHARED_PREFS_DATABASE_SETTINGS)) {
        App.appContext.getSharedPreferences(SHARED_PREFS_DATABASE_SETTINGS, Context.MODE_PRIVATE)
    }

    single<AppDictionaryDatabase> {
        val dbName = LOCAL_DATABASE_DICTIONARY_NAME
        val ctx: Context = get()

        Room.databaseBuilder(ctx, AppDictionaryDatabase::class.java, dbName)
            .fallbackToDestructiveMigration().build()
    }

    single {
        Room.databaseBuilder(
            androidContext(), UserDictionaryDatabase::class.java, USER_DATABASE_DICTIONARY_NAME
        ).fallbackToDestructiveMigration().build()
    }
    Log.d(
        "DEBUG",
        "Room.databaseBuilder после окончания" + " работ над базами данных убрать все инструменты " + "удаления при обновлении и удаления при запуске"
    )

    single<com.sleeplessdog.matchthewords.game.data.database.UserDictionaryDao> {
        get<UserDictionaryDatabase>().userDictionaryDao()
    }

    single {
        UserDictionaryRepository(dao = get())
    }

    single(named(SHARED_PREFS_SCORE_KEY)) {
        App.appContext.getSharedPreferences(SHARED_PREFS_SCORE_REPOSITORY, Context.MODE_PRIVATE)
    }
    single<ScoreRepository> {
        ScoreRepositoryImpl(get(named(SHARED_PREFS_SCORE_KEY)))
    }

    single<ServerDateRepository> {
        ServerDateRepositoryImpl(get(named(SHARED_PREFS_DATABASE_SETTINGS)))
    }

    single<ServerDbInteractor> {
        ServerDbInteractorImpl(get(), get())
    }

    single { AppGroupsDictionary.build(get()) }

    single<WordCategoryDao> {
        get<AppGroupsDictionary>().wordCategoryDao()
    }

    single<WordCategoriesRepository> { WordCategoriesRepositoryImpl(get()) }

    factory { ObserveFeaturedCategoriesUC(get()) }
    factory { ObserveAllCategoriesGroupedUC(get()) }
    factory { ToggleCategoryUC(get()) }
    factory { SaveSelectionUC(get()) }
    factory { CreateUserCategoryUC(get()) }
    factory { DeleteUserCategoryUC(get()) }
    factory { GetSelectedCategoriesUC(get()) }

    factory {
        AddWordToUserDictionaryUC(
            appDictionaryDatabase = get(), userRepository = get()
        )
    }
}
