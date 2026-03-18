package com.sleeplessdog.matchthewords.dictionary.trash

import android.content.Context
import com.sleeplessdog.matchthewords.backend.data.repository.FirebaseAuthRepository

class UserDatabaseSyncController(
    private val context: Context,
    private val firebaseAuthRepository: FirebaseAuthRepository,
)