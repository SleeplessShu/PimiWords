package com.sleeplessdog.matchthewords.dictionary.dictionary_screen

import android.content.Context
import android.util.Log
import androidx.core.content.edit
import androidx.core.net.toUri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageException
import com.google.firebase.storage.ktx.storage
import com.sleeplessdog.matchthewords.backend.data.db.AppDatabaseProvider
import com.sleeplessdog.matchthewords.dictionary.authorisation.DataTransferStatus
import com.sleeplessdog.matchthewords.dictionary.authorisation.DatabaseInstance
import com.sleeplessdog.matchthewords.dictionary.authorisation.DatabaseSyncState
import com.sleeplessdog.matchthewords.utils.ConstantsPaths
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.tasks.await
import java.io.File
import java.io.FileOutputStream

class DatabaseSyncController(
    private val context: Context,
    private val databaseProvider: AppDatabaseProvider,
) {
    private val storage = Firebase.storage

    private val TAG_SYNC = "DB_SYNC"

    private val currentUid: String?
        get() = FirebaseAuth.getInstance().uid

    private val userRef
        get() = currentUid?.let { uid ->
            storage.reference.child("user/$uid/${ConstantsPaths.USER_DATABASE_DICTIONARY_NAME}")
        }

    private val globalRef =
        storage.reference.child(ConstantsPaths.NETWORK_DATABASE_PATH_ON_FIREBASE)

    private val _deployCompleted = MutableSharedFlow<DatabaseInstance>(extraBufferCapacity = 1)
    val deployCompleted: SharedFlow<DatabaseInstance> = _deployCompleted

    fun getUid() = currentUid ?: "unknown"

    /*private val uid = FirebaseAuth.getInstance().uid
    private val userRef =
        storage.reference.child("user/${uid}/${ConstantsPaths.USER_DATABASE_DICTIONARY_NAME}")
*/
    private val prefs = context.getSharedPreferences(
        ConstantsPaths.SHARED_PREFS_DATABASE_SETTINGS, Context.MODE_PRIVATE
    )

    private val globalDbFile =
        context.getDatabasePath(ConstantsPaths.GLOBAL_DATABASE_DICTIONARY_NAME)

    private val userDbFile = context.getDatabasePath(ConstantsPaths.USER_DATABASE_DICTIONARY_NAME)

    private val _syncState = MutableStateFlow(DatabaseSyncState())
    val syncState: StateFlow<DatabaseSyncState> = _syncState


    suspend fun prepareGlobalDatabaseOnly() {
        checkGlobalDatabase()
    }

    suspend fun checkGlobalDatabase() {

        _syncState.update {
            it.copy(globalDb = DataTransferStatus.REFRESHING)
        }

        try {

            val metadata = globalRef.metadata.await()

            val serverDate = metadata.updatedTimeMillis
            val localDate = prefs.getLong(ConstantsPaths.GLOBAL_DATABASE_DICTIONARY_DATE, 0)

            if (!globalDbFile.exists() || serverDate > localDate) {

                downloadGlobalDatabase(serverDate)

            } else {

                _syncState.update {
                    it.copy(globalDb = DataTransferStatus.SUCCESS)
                }
            }

        } catch (e: Exception) {

            if (!globalDbFile.exists()) {

                copyGlobalFromAssets()

            } else {

                _syncState.update {
                    it.copy(globalDb = DataTransferStatus.NOT_CONNECTED)
                }
            }
        }
    }


    suspend fun checkUserDatabase() {
        val uid = currentUid
        Log.d(TAG_SYNC, "checkUserDatabase called, uid=$uid")

        val ref = userRef

        if (ref == null) {
            Log.d(TAG_SYNC, "checkUserDatabase: ref is null, uid is null!")
            _syncState.update { it.copy(userDb = DataTransferStatus.ERROR) }
            return
        }

        _syncState.update {
            it.copy(userDb = DataTransferStatus.REFRESHING)
        }

        val localExists = userDbFile.exists()

        val localDate = prefs.getLong(ConstantsPaths.USER_DATABASE_DICTIONARY_DATE, 0)

        val localSize = if (localExists) userDbFile.length() else 0L

        try {

            val metadata = ref.metadata.await()

            val serverDate = metadata.updatedTimeMillis
            val serverSize = metadata.sizeBytes

            if (!localExists) {

                _syncState.update {
                    it.copy(userDb = DataTransferStatus.DOWNLOADING)
                }

                downloadUserDatabase(serverDate)

                _syncState.update {
                    it.copy(userDb = DataTransferStatus.NETWORK)
                }

                return
            }

            if (localDate > serverDate) {

                _syncState.update {
                    it.copy(userDb = DataTransferStatus.UPLOADING)
                }

                uploadUserDatabase(localDate)

                _syncState.update {
                    it.copy(userDb = DataTransferStatus.ASSETS)
                }

                return
            }

            if (serverDate > localDate && serverSize > localSize) {

                _syncState.update {
                    it.copy(userDb = DataTransferStatus.DOWNLOADING)
                }

                downloadUserDatabase(serverDate)

                _syncState.update {
                    it.copy(userDb = DataTransferStatus.NETWORK)
                }

                return
            }

            _syncState.update {
                it.copy(userDb = DataTransferStatus.SUCCESS)
            }

        } catch (e: StorageException) {

            if (localExists) {

                _syncState.update {
                    it.copy(userDb = DataTransferStatus.ASSETS)
                }

                if (e.errorCode == StorageException.ERROR_OBJECT_NOT_FOUND) {

                    // файла на сервере нет — делаем первый backup
                    _syncState.update {
                        it.copy(userDb = DataTransferStatus.UPLOADING)
                    }

                    uploadUserDatabase(localDate)

                    _syncState.update {
                        it.copy(userDb = DataTransferStatus.ASSETS)
                    }
                }

            } else {

                _syncState.update {
                    it.copy(userDb = DataTransferStatus.NOT_CONNECTED)
                }
            }
        }
    }


    private suspend fun downloadGlobalDatabase(serverDate: Long) {

        _syncState.update {
            it.copy(globalDb = DataTransferStatus.DOWNLOADING)
        }

        val temp = File(context.cacheDir, "temp_global_db")

        globalRef.getFile(temp).await()

        deployDatabase(temp, globalDbFile, DatabaseInstance.GLOBAL)

        prefs.edit {
            putLong(ConstantsPaths.GLOBAL_DATABASE_DICTIONARY_DATE, serverDate)
        }

        _syncState.update {
            it.copy(globalDb = DataTransferStatus.NETWORK)
        }

        temp.delete()
    }


    private suspend fun copyGlobalFromAssets() {

        val temp = File(context.cacheDir, "temp_assets_db")

        context.assets.open(ConstantsPaths.ASSETS_DATABASE_DICTIONARY_PATH).use { input ->
            FileOutputStream(temp).use { output ->
                input.copyTo(output)
            }
        }

        deployDatabase(temp, globalDbFile, DatabaseInstance.GLOBAL)

        prefs.edit {
            putLong(ConstantsPaths.GLOBAL_DATABASE_DICTIONARY_DATE, 0)
        }

        _syncState.update {
            it.copy(globalDb = DataTransferStatus.ASSETS)
        }

        temp.delete()
    }


    private suspend fun downloadUserDatabase(serverDate: Long) {
        val ref = userRef
        if (ref == null) {
            _syncState.update { it.copy(userDb = DataTransferStatus.ERROR) }
            return
        }
        val temp = File(context.cacheDir, "temp_user_db")

        ref.getFile(temp).await()

        deployDatabase(temp, userDbFile, DatabaseInstance.USER)

        prefs.edit {
            putLong(ConstantsPaths.USER_DATABASE_DICTIONARY_DATE, serverDate)
        }

        _syncState.update {
            it.copy(userDb = DataTransferStatus.NETWORK)
        }

        temp.delete()
    }


    private suspend fun uploadUserDatabase(localDate: Long) {
        val ref = userRef
        if (ref == null) {
            _syncState.update { it.copy(userDb = DataTransferStatus.ERROR) }
            return
        }

        val uid = currentUid ?: run {
            Log.e(TAG_SYNC, "UPLOAD FAILED: uid is null")
            _syncState.update { it.copy(userDb = DataTransferStatus.ERROR) }
            return
        }
        val temp = File(context.cacheDir, "upload_user_db")
        Log.d(TAG_SYNC, "UPLOAD PATH user/$uid/${ConstantsPaths.USER_DATABASE_DICTIONARY_NAME}")
        databaseProvider.withUserDatabaseLock {

            databaseProvider.closeUserDatabase()

            File(userDbFile.path + "-wal").delete()
            File(userDbFile.path + "-shm").delete()

            userDbFile.copyTo(temp, overwrite = true)

            databaseProvider.openUserDatabase()
        }

        ref.putFile(temp.toUri()).await()

        prefs.edit {
            putLong(ConstantsPaths.USER_DATABASE_DICTIONARY_DATE, localDate)
        }

        temp.delete()
    }


    private suspend fun deployDatabase(temp: File, target: File, type: DatabaseInstance) {

        Log.d(TAG_SYNC, "deployDatabase START type=$type")
        Log.d(TAG_SYNC, "temp exists=${temp.exists()} size=${temp.length()}")
        Log.d(TAG_SYNC, "target path=${target.absolutePath}")

        when (type) {

            DatabaseInstance.GLOBAL -> {

                Log.d(TAG_SYNC, "GLOBAL DEPLOY BEGIN")

                _syncState.update {
                    Log.d(TAG_SYNC, "STATE -> DEPLOYING GLOBAL")
                    it.copy(globalDb = DataTransferStatus.DEPLOYING)
                }

                databaseProvider.withGlobalDatabaseLock {

                    Log.d(TAG_SYNC, "LOCK ACQUIRED GLOBAL")
                    delay(100)
                    Log.d(TAG_SYNC, "Closing global database")
                    databaseProvider.closeGlobalDatabase()
                    Log.d(TAG_SYNC, "Global database closed")

                    val wal = File(target.path + "-wal")
                    val shm = File(target.path + "-shm")

                    Log.d(TAG_SYNC, "Deleting WAL=${wal.exists()} SHM=${shm.exists()}")

                    wal.delete()
                    shm.delete()

                    Log.d(TAG_SYNC, "Ensuring parent dir")
                    target.parentFile?.mkdirs()

                    Log.d(TAG_SYNC, "Copying temp -> target")

                    temp.copyTo(target, overwrite = true)

                    Log.d(TAG_SYNC, "Copy finished size=${target.length()}")

                    Log.d(TAG_SYNC, "Opening global database")
                    databaseProvider.openGlobalDatabase()

                    Log.d(TAG_SYNC, "Global database reopened")
                }

                Log.d(TAG_SYNC, "LOCK RELEASED GLOBAL")
            }

            DatabaseInstance.USER -> {

                Log.d(TAG_SYNC, "USER DEPLOY BEGIN")

                _syncState.update {
                    Log.d(TAG_SYNC, "STATE -> DEPLOYING USER")
                    it.copy(userDb = DataTransferStatus.DEPLOYING)
                }

                databaseProvider.withUserDatabaseLock {

                    Log.d(TAG_SYNC, "LOCK ACQUIRED USER")

                    Log.d(TAG_SYNC, "Closing user database")
                    databaseProvider.closeUserDatabase()

                    Log.d(TAG_SYNC, "User database closed")

                    val wal = File(target.path + "-wal")
                    val shm = File(target.path + "-shm")

                    Log.d(TAG_SYNC, "Deleting WAL=${wal.exists()} SHM=${shm.exists()}")

                    wal.delete()
                    shm.delete()

                    Log.d(TAG_SYNC, "Ensuring parent dir")
                    target.parentFile?.mkdirs()

                    Log.d(TAG_SYNC, "Copying temp -> target")

                    temp.copyTo(target, overwrite = true)

                    Log.d(TAG_SYNC, "Copy finished size=${target.length()}")

                    Log.d(TAG_SYNC, "Opening user database")
                    databaseProvider.openUserDatabase()

                    Log.d(TAG_SYNC, "User database reopened")
                }

                Log.d(TAG_SYNC, "LOCK RELEASED USER")
            }
        }
        Log.d(TAG_SYNC, "deployDatabase END type=$type")
        _deployCompleted.emit(type)

        Log.d(TAG_SYNC, "deployDatabase END type=$type")
    }
}
