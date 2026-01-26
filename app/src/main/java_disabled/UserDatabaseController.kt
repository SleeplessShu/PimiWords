package com.sleeplessdog.matchthewords.game.presentation.controller

import android.content.Context
import android.net.Uri
import androidx.sqlite.db.SimpleSQLiteQuery
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.storage.StorageException
import com.google.firebase.storage.storage
import com.sleeplessdog.matchthewords.game.data.database.UserDictionaryDatabase
import com.sleeplessdog.matchthewords.utils.ConstantsPaths.USER_DATABASE_DICTIONARY_NAME
import kotlinx.coroutines.tasks.await
import java.io.File

class UserDatabaseController(
    private val userDbProvider: UserDictionaryDatabase,
    private val context: Context,
) {
    private val userDb = userDbProvider
    private val storage = Firebase.storage
    private val auth = Firebase.auth
    private val dbName = USER_DATABASE_DICTIONARY_NAME
    private val localFile by lazy { context.getDatabasePath(dbName) }

    private fun getStorageRef() = auth.currentUser?.let { user ->
        storage.reference.child("users/${user.uid}/user_backup.db")
    }

    /**
     * Сохраняет БД пользователя в облако.
     * После успешного выполнения БД будет закрыта, но НЕ переоткрыта.
     */
    suspend fun backupToCloud(): Result<Unit> {
        val ref = getStorageRef() ?: return Result.failure(Exception("Пользователь не авторизован"))

        return try {
            // 1. Сливаем WAL-файлы в основной файл БД
            userDb.checkpoint()

            // 2. Закрываем БД, чтобы гарантировать целостность файла
            userDb.close()

            // 3. Отправляем файл
            ref.putFile(Uri.fromFile(localFile)).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Восстанавливает БД пользователя из облака.
     * @return Result<Boolean> - `true`, если бэкап был найден и восстановлен, `false`, если бэкапа нет.
     * Failure в случае других ошибок (сеть, диск и т.д.).
     * Важно: после успешного восстановления БД будет закрыта. Приложение потребует перезапуска.
     */
    suspend fun restoreFromCloud(): Result<Boolean> {
        val ref = getStorageRef() ?: return Result.failure(Exception("Пользователь не авторизован"))

        // Шаг 1: Проверяем, существует ли бэкап
        try {
            ref.metadata.await()
        } catch (e: StorageException) {
            if (e.errorCode == StorageException.ERROR_OBJECT_NOT_FOUND) {
                return Result.success(false) // Бэкапа нет, это не ошибка.
            }
            return Result.failure(e) // Другая ошибка Firebase (нет сети, прав и т.д.)
        } catch (e: Exception) {
            return Result.failure(e) // Другая непредвиденная ошибка
        }

        // Шаг 2: Скачиваем и заменяем файл, если он есть
        val tempFile = File.createTempFile("restored_user_db", ".tmp", context.cacheDir)
        return try {
            ref.getFile(tempFile).await()

            userDb.close() // Закрываем соединение с текущей БД
            tempFile.copyTo(localFile, overwrite = true)

            // Удаляем вспомогательные файлы от старой БД
            File(localFile.path + "-wal").delete()
            File(localFile.path + "-shm").delete()

            Result.success(true) // Все прошло успешно
        } catch (e: Exception) {
            Result.failure(e) // Ошибка при скачивании или копировании
        } finally {
            tempFile.delete() // Гарантированно удаляем временный файл
        }
    }

    /**
     * Принудительно выполняет checkpoint для WAL, сливая данные в основной .db файл.
     */
    private fun UserDictionaryDatabase.checkpoint() {
        if (!isOpen) return
        query(SimpleSQLiteQuery("PRAGMA wal_checkpoint(FULL)"))
    }
}