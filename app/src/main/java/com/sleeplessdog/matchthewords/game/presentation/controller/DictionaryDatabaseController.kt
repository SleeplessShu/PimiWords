package com.sleeplessdog.matchthewords.game.presentation.controller


import android.content.Context
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.tasks.await
import java.io.File
import java.io.FileOutputStream

class DictionaryDatabaseController(private val context: Context) {
    private val storage = Firebase.storage
    private val remoteDbRef = storage.reference.child("actual_db/pimi_dictionary.db")
    private val dbName = "dictionary.db"
    private val localDbFile: File = context.getDatabasePath(dbName)
    private val prefs = context.getSharedPreferences("db_prefs", Context.MODE_PRIVATE)

    suspend fun prepareDatabase(): Result<Unit> {
        return try {
            val metadata = remoteDbRef.metadata.await()
            val serverDate = metadata.updatedTimeMillis
            val localDate = prefs.getLong("local_db_date", 0L)

            if (!localDbFile.exists() || serverDate > localDate) {
                downloadDatabase(serverDate)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            if (!localDbFile.exists()) {
                val assetResult = copyFromAssets()
                if (assetResult) Result.success(Unit)
                else Result.failure(Exception("Failed to copy asset database"))
            } else {
                Result.success(Unit)
            }
        }
    }

    private fun copyFromAssets(): Boolean {
        return try {
            localDbFile.parentFile?.mkdirs()
            context.assets.open("databases/dictionary_default.db").use { input ->
                FileOutputStream(localDbFile).use { output ->
                    input.copyTo(output)
                }
            }

            prefs.edit().putLong("local_db_date", 0L).apply()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    private suspend fun downloadDatabase(newDate: Long) {

        localDbFile.parentFile?.mkdirs()

        val tempFile = File(context.cacheDir, "temp_db")
        remoteDbRef.getFile(tempFile).await()

        tempFile.copyTo(localDbFile, overwrite = true)

        File(localDbFile.path + "-wal").delete()
        File(localDbFile.path + "-shm").delete()

        prefs.edit().putLong("local_db_date", newDate).apply()
        tempFile.delete()
    }
}
