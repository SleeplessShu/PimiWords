package com.sleeplessdog.pimi.trash.trash

import android.content.Context
import com.sleeplessdog.pimi.dictionary.authorisation.FirebaseAuthRepository

class UserDatabaseSyncController(
    private val context: Context,
    private val firebaseAuthRepository: FirebaseAuthRepository,
)