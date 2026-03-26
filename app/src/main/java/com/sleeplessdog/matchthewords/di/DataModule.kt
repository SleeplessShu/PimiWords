package com.sleeplessdog.matchthewords.di

import android.content.Context
import android.os.Handler
import android.os.Looper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.sleeplessdog.matchthewords.backend.data.repository.FirebaseAuthRepository
import com.sleeplessdog.matchthewords.utils.ConstantsPaths.FIREBASE_KEY
import com.sleeplessdog.matchthewords.utils.ConstantsPaths.SHARED_PREFS_DATABASE_SETTINGS
import com.sleeplessdog.matchthewords.utils.ConstantsPaths.SHARED_PREFS_SCORE_KEY
import com.sleeplessdog.matchthewords.utils.ConstantsPaths.SHARED_PREFS_SCORE_REPOSITORY
import com.sleeplessdog.matchthewords.utils.ConstantsPaths.SHARED_PREFS_THEME_KEY
import com.sleeplessdog.matchthewords.utils.ConstantsPaths.SHARED_PREFS_THEME_REPOSITORY
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module

val dataModule = module {

    factory<Handler> {
        Handler(Looper.getMainLooper())
    }

    single {
        FirebaseDatabase.getInstance(FIREBASE_KEY)
    }

    single {
        FirebaseStorage.getInstance()
    }

    single {
        FirebaseFirestore.getInstance()
    }

    single(named(SHARED_PREFS_THEME_KEY)) {
        androidContext().getSharedPreferences(SHARED_PREFS_THEME_REPOSITORY, Context.MODE_PRIVATE)
    }

    single(named(SHARED_PREFS_DATABASE_SETTINGS)) {
        androidContext().getSharedPreferences(SHARED_PREFS_DATABASE_SETTINGS, Context.MODE_PRIVATE)
    }

    single(named(SHARED_PREFS_SCORE_KEY)) {
        androidContext().getSharedPreferences(SHARED_PREFS_SCORE_REPOSITORY, Context.MODE_PRIVATE)
    }

    single<FirebaseAuthRepository> { FirebaseAuthRepository(get()) }
    single { FirebaseAuth.getInstance() }
}
