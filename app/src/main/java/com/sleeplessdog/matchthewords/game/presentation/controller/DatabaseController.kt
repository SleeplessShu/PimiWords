package com.sleeplessdog.matchthewords.game.presentation.controller


import android.content.Context
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.sleeplessdog.matchthewords.game.data.database.AppDatabase
import java.io.File

class DatabaseController(val context: Context) {
    private val storage = Firebase.storage

    // Путь к вашей общей базе в Firebase Storage
    private val remoteDbRef = storage.reference.child("databases/general_data.db")

    // Имя файла должно совпадать с тем, что вы передаете в Room.databaseBuilder
    private val dbName = "my_database.db"
    private val localDbFile: File = context.getDatabasePath(dbName)

    private val prefs = context.getSharedPreferences("db_prefs", Context.MODE_PRIVATE)

    fun checkAndSyncDatabase(onComplete: (Boolean) -> Unit) {
        remoteDbRef.metadata.addOnSuccessListener { metadata ->
            val serverTime = metadata.updatedTimeMillis
            val lastUpdateTime = prefs.getLong("last_db_update", 0L)

            if (serverTime > lastUpdateTime) {
                downloadAndReplace(serverTime, onComplete)
            } else {
                onComplete(false) // Обновление не требуется
            }
        }.addOnFailureListener {
            onComplete(false) // Ошибка связи
        }
    }

    private fun downloadAndReplace(newTime: Long, onComplete: (Boolean) -> Unit) {
        val tempFile = File.createTempFile("update", ".db", context.cacheDir)

        remoteDbRef.getFile(tempFile).addOnSuccessListener {
            try {

                AppDatabase.getInstance(context).close()

                // 2. Копируем файл
                tempFile.copyTo(localDbFile, overwrite = true)

                // 3. Удаляем вспомогательные файлы Room (WAL-режим)
                // Если их не удалить, Room может попытаться восстановить старые данные
                val walFile = File(localDbFile.path + "-wal")
                val shmFile = File(localDbFile.path + "-shm")
                if (walFile.exists()) walFile.delete()
                if (shmFile.exists()) shmFile.delete()

                // 4. Сохраняем новую дату в конфиг
                prefs.edit().putLong("last_db_update", newTime).apply()

                tempFile.delete()
                onComplete(true)
            } catch (e: Exception) {
                e.printStackTrace()
                onComplete(false)
            }
        }
    }
}
