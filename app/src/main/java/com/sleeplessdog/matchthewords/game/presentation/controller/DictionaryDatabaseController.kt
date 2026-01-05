package com.sleeplessdog.matchthewords.game.presentation.controller

import android.content.Context
import android.util.Log
import androidx.core.content.edit
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.sleeplessdog.matchthewords.utils.ConstantsPaths.ASSETS_DATABASE_DICTIONARY_PATH
import com.sleeplessdog.matchthewords.utils.ConstantsPaths.LOCAL_DATABASE_DICTIONARY_DATE
import com.sleeplessdog.matchthewords.utils.ConstantsPaths.LOCAL_DATABASE_DICTIONARY_NAME
import com.sleeplessdog.matchthewords.utils.ConstantsPaths.NETWORK_DATABASE_PATH_ON_FIREBASE
import com.sleeplessdog.matchthewords.utils.ConstantsPaths.SHARED_PREFS_DATABASE_SETTINGS
import kotlinx.coroutines.tasks.await
import java.io.File
import java.io.FileOutputStream

class DictionaryDatabaseController(private val context: Context) {
    private val storage = Firebase.storage
    private val remoteDbRef = storage.reference.child(NETWORK_DATABASE_PATH_ON_FIREBASE)
    private val dbName = LOCAL_DATABASE_DICTIONARY_NAME
    private val localDbFile: File = context.getDatabasePath(dbName)
    private val prefs =
        context.getSharedPreferences(SHARED_PREFS_DATABASE_SETTINGS, Context.MODE_PRIVATE)

    suspend fun prepareDatabase(): Result<Unit> {
        return try {
            val metadata = remoteDbRef.metadata.await()
            val serverDate = metadata.updatedTimeMillis
            val localDate = prefs.getLong(LOCAL_DATABASE_DICTIONARY_DATE, 0L)

            if (!localDbFile.exists() || serverDate > localDate) {
                downloadDatabase(serverDate)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            if (!localDbFile.exists()) {
                val assetResult = copyFromAssets()
                if (assetResult) Result.success(Unit)
                else Result.failure(Exception("Failed to copy asset database $e"))
            } else {
                Result.success(Unit)
            }
        }
    }

    private fun copyFromAssets(): Boolean {
        return try {
            localDbFile.parentFile?.mkdirs()
            context.assets.open(ASSETS_DATABASE_DICTIONARY_PATH).use { input ->
                FileOutputStream(localDbFile).use { output ->
                    input.copyTo(output)
                }
            }

            prefs.edit { putLong(LOCAL_DATABASE_DICTIONARY_DATE, 0L) }
            true
        } catch (e: Exception) {
            Log.d("DEBUG", "copyFromAssets: $e")
            false
        }
    }

    private suspend fun downloadDatabase(newDate: Long) {

        localDbFile.parentFile?.mkdirs()

        val tempFile = File(context.cacheDir, "temp_db")
        try {
            remoteDbRef.getFile(tempFile).await()

            tempFile.copyTo(localDbFile, overwrite = true)

            val resultWal = File(localDbFile.path + "-wal").delete()
            Log.d("DEBUG", "downloadDatabase: $resultWal")

            val resultShm = File(localDbFile.path + "-shm").delete()
            Log.d("DEBUG", "downloadDatabase: $resultShm")

            prefs.edit { putLong(LOCAL_DATABASE_DICTIONARY_DATE, newDate) }
        } finally {
            val result = tempFile.delete()
            Log.d("DEBUG", "downloadDatabase: $result")
        }
    }
}
