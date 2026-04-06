package com.sleeplessdog.pimi.dictionary.authorisation

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.sleeplessdog.pimi.utils.ConstantsPaths.TAG_AUTH
import kotlinx.coroutines.tasks.await

class FirebaseAuthRepository(
    private val firebaseAuth: FirebaseAuth,
) {
    val currentUser get() = firebaseAuth.currentUser

    fun isUserAuthorized(): Boolean {
        return firebaseAuth.currentUser != null
    }

    suspend fun signInWithGoogle(idToken: String): Result<Unit> {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            firebaseAuth.signInWithCredential(credential).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.d(TAG_AUTH, "signInWithGoogle: ${e}")
            Result.failure(e)
        }
    }
}