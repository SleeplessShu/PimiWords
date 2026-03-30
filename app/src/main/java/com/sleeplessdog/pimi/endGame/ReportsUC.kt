package com.sleeplessdog.pimi.endGame

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.sleeplessdog.pimi.games.data.repository.AppPrefs
import kotlinx.coroutines.tasks.await

class ReportWordMistakeUC(
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth,
    private val appPrefs: AppPrefs,
) {
    suspend operator fun invoke(wordIds: List<Int>): Result<Unit> {
        return try {
            val report = hashMapOf(
                "wordIds" to wordIds,
                "uid" to (firebaseAuth.currentUser?.uid ?: "anonymous"),
                "uiLanguage" to appPrefs.getUiLanguage().name,
                "studyLanguage" to appPrefs.getStudyLanguage().name,
                "timestamp" to System.currentTimeMillis()
            )
            firestore.collection("word_reports")
                .add(report)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
