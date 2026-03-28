package com.sleeplessdog.pimi.dictionary.dictionary_screen

import com.sleeplessdog.pimi.dictionary.authorisation.FirebaseAuthRepository

class FirebaseAuthController(
    private val authRepository: FirebaseAuthRepository,
) {

    fun isUserAuthorized(): Boolean {
        val user = authRepository.currentUser ?: return false
        return user.providerData.any { it.providerId == "google.com" }
    }

    suspend fun signInWithGoogle(idToken: String): Result<Unit> {
        return authRepository.signInWithGoogle(idToken)
    }
}