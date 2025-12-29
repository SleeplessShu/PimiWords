package com.sleeplessdog.matchthewords

import android.content.Context
import android.content.SharedPreferences
import android.os.Handler
import android.util.Log
import androidx.room.Room
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.sleeplessdog.matchthewords.game.data.database.AppDatabase
import com.sleeplessdog.matchthewords.game.data.database.UserDictionaryDatabase
import com.sleeplessdog.matchthewords.game.data.database.WordCategoryDao
import com.sleeplessdog.matchthewords.game.data.database.resolveAssetDatabase
import com.sleeplessdog.matchthewords.game.data.repositories.ScoreRepositoryImpl
import com.sleeplessdog.matchthewords.game.data.repositories.UserDictionaryRepository
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
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module


val dataModule = module {

    single {
        FirebaseDatabase.getInstance("https://match-the-words-d26c2-default-rtdb.europe-west1.firebasedatabase.app")
    }

    single {
        FirebaseStorage.getInstance()
    }

    single(named("themePreferences")) {
        App.appContext.getSharedPreferences("NightMode", Context.MODE_PRIVATE)
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

    single(named("db_prefs")) {
        App.appContext.getSharedPreferences("db_prefs", Context.MODE_PRIVATE)
    }

    single {
        val dbName = "dictionary.db"
        val ctx: Context = get()

        val sel = resolveAssetDatabase(ctx) // получаем и путь, и дату

        val prefs: SharedPreferences = get(qualifier = named("db_prefs"))
        prefs.edit().putString("local_db_date", sel.date).apply()

        Room.databaseBuilder(ctx, AppDatabase::class.java, dbName)
            .createFromAsset(sel.assetPath)
            .build()
    }

    single {
        Room.databaseBuilder(
            androidContext(),
            UserDictionaryDatabase::class.java,
            "user_dictionary_db" // Имя файла БД
        )
            .fallbackToDestructiveMigration() // При обновлении версии, старая БД будет удалена..
            .build()
        Log.d(
            "DEBUG", "Room.databaseBuilder после окончания" +
                    " работ над базами данных убрать все инструменты " +
                    "удаления при обновлении и удаления при запуске"
        )
    }

    single {
        get<UserDictionaryDatabase>().userDictionaryDao()
    }

    single {
        UserDictionaryRepository(get())
    }

    single(named("scoreStore")) {
        App.appContext.getSharedPreferences("ScoreHistory", Context.MODE_PRIVATE)
    }
    single<ScoreRepository> {
        ScoreRepositoryImpl(get(named("scoreStore")))
    }

    single<ServerDateRepository> {
        ServerDateRepositoryImpl(get(named("db_prefs")))
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