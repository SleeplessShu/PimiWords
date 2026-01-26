package com.sleeplessdog.matchthewords.server.domain


import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.File
import kotlin.coroutines.resume

class ServerDbInteractorImpl(
    private val database: FirebaseDatabase, private val storage: FirebaseStorage
) : ServerDbInteractor {

    override suspend fun checkForUpdate(localDate: String): Result<String?> =
        suspendCancellableCoroutine { cont ->
            val ref = database.getReference(DB_DATE_KEY)
            ref.get().addOnSuccessListener { snapshot ->
                val serverDate = snapshot.getValue(String::class.java)
                if (serverDate != null && serverDate > localDate) {
                    cont.resume(Result.success(serverDate))
                } else {
                    cont.resume(Result.success(null))
                }
            }.addOnFailureListener {
                cont.resume(Result.failure(it))
            }
        }



    override suspend fun downloadDatabase(localFile: File): Result<Unit> =
        suspendCancellableCoroutine { cont ->
            val ref = storage.reference.child(STORAGE_PATH)
            ref.getFile(localFile)
                .addOnSuccessListener { cont.resume(Result.success(Unit)) }
                .addOnFailureListener { cont.resume(Result.failure(it)) }
        }


    private companion object {
        val DB_DATE_KEY = "db_last_update_date"
        val STORAGE_PATH = "databases/latest_db.db"
    }
}