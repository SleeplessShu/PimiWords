package com.sleeplessdog.pimi.dictionary.dictionary_screen

import android.content.Context
import android.util.Log
import androidx.core.content.edit
import androidx.core.net.toUri
import androidx.sqlite.db.SimpleSQLiteQuery
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageException
import com.google.firebase.storage.ktx.storage
import com.sleeplessdog.pimi.database.AppDatabaseProvider
import com.sleeplessdog.pimi.database.global.GlobalDictionaryEntity
import com.sleeplessdog.pimi.database.user.UserGroupEntity
import com.sleeplessdog.pimi.dictionary.authorisation.DataTransferStatus
import com.sleeplessdog.pimi.dictionary.authorisation.DatabaseInstance
import com.sleeplessdog.pimi.dictionary.authorisation.DatabaseSyncState
import com.sleeplessdog.pimi.games.data.repository.AppPrefs
import com.sleeplessdog.pimi.utils.ConstantsPaths
import com.sleeplessdog.pimi.utils.ConstantsPaths.PREFS_NAME
import com.sleeplessdog.pimi.utils.DictionaryDestinations.DICTIONARY_TEMP_ASSETS
import com.sleeplessdog.pimi.utils.DictionaryDestinations.DICTIONARY_TEMP_GLOBAL
import com.sleeplessdog.pimi.utils.DictionaryDestinations.DICTIONARY_TEMP_USER_DB
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
    private val appPrefs: AppPrefs,
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

    private val _pendingDeployReady = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val pendingDeployReady: SharedFlow<Unit> = _pendingDeployReady

    private val _deployCompleted = MutableSharedFlow<DatabaseInstance>(extraBufferCapacity = 1)
    val deployCompleted: SharedFlow<DatabaseInstance> = _deployCompleted

    fun getUid() = currentUid ?: "unknown"

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

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

            if (serverDate > localDate) {

                downloadGlobalDatabase(serverDate)

            } else {

                _syncState.update {
                    it.copy(globalDb = DataTransferStatus.ASSETS)
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
        val ref = userRef

        if (ref == null) {
            _syncState.update { it.copy(userDb = DataTransferStatus.ERROR) }
            return
        }

        _syncState.update { it.copy(userDb = DataTransferStatus.REFRESHING) }

        val localExists = userDbFile.exists() && userDbFile.length() > 0
        val localDate = prefs.getLong(ConstantsPaths.USER_DATABASE_DICTIONARY_DATE, 0)

        try {
            val metadata = ref.metadata.await()
            val serverDate = metadata.updatedTimeMillis
            val needToUpload = appPrefs.getLocalDatabaseDirty()
            Log.d(
                TAG_SYNC,
                "localExists=$localExists localDate=$localDate fileSize=${userDbFile.length()}"
            )
            when {

                !localExists -> {
                    Log.d(TAG_SYNC, "local empty — downloading from server")
                    _syncState.update { it.copy(userDb = DataTransferStatus.DOWNLOADING) }
                    Log.d("DEPLOY_DEBUG", "checkUserDatabase: downloading")
                    downloadUserDatabase(serverDate)
                    _syncState.update { it.copy(userDb = DataTransferStatus.NETWORK) }
                }

                localDate > serverDate && needToUpload -> {
                    Log.d(TAG_SYNC, "local newer — uploading to server")
                    _syncState.update { it.copy(userDb = DataTransferStatus.UPLOADING) }
                    Log.d("DEPLOY_DEBUG", "checkUserDatabase: uploading")
                    uploadUserDatabase()
                    _syncState.update { it.copy(userDb = DataTransferStatus.ASSETS) }
                }

                serverDate > localDate -> {
                    Log.d(TAG_SYNC, "server newer — downloading")
                    _syncState.update { it.copy(userDb = DataTransferStatus.DOWNLOADING) }
                    Log.d("DEPLOY_DEBUG", "checkUserDatabase: downloading")
                    downloadUserDatabase(serverDate)
                    _syncState.update { it.copy(userDb = DataTransferStatus.NETWORK) }
                }

                else -> {
                    Log.d(TAG_SYNC, "in sync — nothing to do")
                    _syncState.update { it.copy(userDb = DataTransferStatus.SUCCESS) }
                }
            }

        } catch (e: StorageException) {
            when {
                e.errorCode == StorageException.ERROR_OBJECT_NOT_FOUND && localExists -> {
                    Log.d(TAG_SYNC, "not on server — uploading")
                    _syncState.update { it.copy(userDb = DataTransferStatus.UPLOADING) }
                    Log.d("DEPLOY_DEBUG", "checkUserDatabase: uploading")
                    uploadUserDatabase()
                    _syncState.update { it.copy(userDb = DataTransferStatus.ASSETS) }
                }

                !localExists -> {
                    Log.d(TAG_SYNC, "no network, no local data")
                    _syncState.update { it.copy(userDb = DataTransferStatus.NOT_CONNECTED) }
                }

                else -> {
                    Log.d(TAG_SYNC, "no network, using local")
                    _syncState.update { it.copy(userDb = DataTransferStatus.ASSETS) }
                }
            }
        }
    }


    private suspend fun downloadGlobalDatabase(serverDate: Long) {

        _syncState.update {
            it.copy(globalDb = DataTransferStatus.DOWNLOADING)
        }

        val temp = File(context.cacheDir, DICTIONARY_TEMP_GLOBAL)

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

        val temp = File(context.cacheDir, DICTIONARY_TEMP_ASSETS)

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
        val ref = userRef ?: run {
            _syncState.update { it.copy(userDb = DataTransferStatus.ERROR) }
            return
        }

        val temp = File(context.cacheDir, DICTIONARY_TEMP_USER_DB)
        ref.getFile(temp).await()

        if (!temp.exists() || temp.length() < 1024) {
            Log.e(TAG_SYNC, "downloaded file too small: ${temp.length()}")
            temp.delete()
            _syncState.update { it.copy(userDb = DataTransferStatus.ERROR) }
            return
        }

        deployDatabase(temp, userDbFile, DatabaseInstance.USER)

        prefs.edit {
            putLong(ConstantsPaths.USER_DATABASE_DICTIONARY_DATE, serverDate)
        }

        _syncState.update { it.copy(userDb = DataTransferStatus.NETWORK) }
        temp.delete()
    }


    private suspend fun uploadUserDatabase() {
        val ref = userRef ?: run {
            _syncState.update { it.copy(userDb = DataTransferStatus.ERROR) }
            return
        }

        val temp = File(context.cacheDir, "upload_user_db")

        try {
            val db = databaseProvider.getUserDatabase()
            val supportDb = db.openHelper.writableDatabase
            val dbPath = supportDb.path

            val cursor = supportDb.query(SimpleSQLiteQuery("PRAGMA wal_checkpoint(TRUNCATE)"))
            val row = if (cursor.moveToFirst()) {
                Triple(cursor.getInt(0), cursor.getInt(1), cursor.getInt(2))
            } else null
            cursor.close()

            Log.d(
                TAG_SYNC,
                "checkpoint result: blocked=${row?.first} written=${row?.second} remaining=${row?.third}"
            )

            val mainFile = File(dbPath)
            val walFile = File("$dbPath-wal")

            mainFile.copyTo(temp, overwrite = true)

            if (walFile.exists() && walFile.length() > 0) {

                val walCopy = File(context.cacheDir, "upload_wal")
                walFile.copyTo(walCopy, overwrite = true)

                val tempCopy = File(context.cacheDir, "upload_copy.db")
                mainFile.copyTo(tempCopy, overwrite = true)
                walCopy.copyTo(File(tempCopy.path + "-wal"), overwrite = true)

                val copyDb = android.database.sqlite.SQLiteDatabase.openDatabase(
                    tempCopy.path, null, android.database.sqlite.SQLiteDatabase.OPEN_READWRITE
                )
                copyDb.execSQL("PRAGMA wal_checkpoint(TRUNCATE)")
                copyDb.close()

                tempCopy.copyTo(temp, overwrite = true)

                walCopy.delete()
                tempCopy.delete()
                File(tempCopy.path + "-wal").delete()
                File(tempCopy.path + "-shm").delete()
            }

        } catch (e: Exception) {
            Log.e(TAG_SYNC, "upload preparation failed: $e")
            _syncState.update { it.copy(userDb = DataTransferStatus.ERROR) }
            return
        }

        if (!temp.exists() || temp.length() < 1024) {
            _syncState.update { it.copy(userDb = DataTransferStatus.ERROR) }
            return
        }

        ref.putFile(temp.toUri()).await()

        prefs.edit {
            putLong(ConstantsPaths.USER_DATABASE_DICTIONARY_DATE, System.currentTimeMillis())
        }
        Log.d(TAG_SYNC, "upload complete, size=${temp.length()}")
        appPrefs.markLocalDatabaseClear()
        temp.delete()

    }


    private suspend fun deployDatabase(temp: File, target: File, type: DatabaseInstance) {

        Log.d(TAG_SYNC, "deployDatabase START type=$type")
        Log.d(TAG_SYNC, "temp exists=${temp.exists()} size=${temp.length()}")
        Log.d(TAG_SYNC, "target path=${target.absolutePath}")

        when (type) {

            DatabaseInstance.GLOBAL -> {

                _syncState.update {
                    it.copy(globalDb = DataTransferStatus.DEPLOYING)
                }

                validateAndPatchSchema(temp)
                
                databaseProvider.withGlobalDatabaseLock {

                    delay(100)
                    databaseProvider.closeGlobalDatabase()

                    val wal = File(target.path + "-wal")
                    val shm = File(target.path + "-shm")

                    wal.delete()
                    shm.delete()

                    target.parentFile?.mkdirs()

                    temp.copyTo(target, overwrite = true)

                    databaseProvider.openGlobalDatabase()
                }
            }

            DatabaseInstance.USER -> {
                Log.d("DEPLOY_DEBUG", "deployDatabase USER called from:")
                Log.d("DEPLOY_DEBUG", Thread.currentThread().stackTrace.take(10).joinToString("\n"))
                _syncState.update { it.copy(userDb = DataTransferStatus.DEPLOYING) }

                val pendingFile = File(context.cacheDir, "pending_user_db")
                temp.copyTo(pendingFile, overwrite = true)

                prefs.edit {
                    putBoolean("pending_user_db_deploy", true)
                }
                _pendingDeployReady.emit(Unit)
                _deployCompleted.emit(type)
            }
        }
    }


    suspend fun ensureSavedWordsGroup() {
        val userDao = databaseProvider.getUserDatabase().userDao()
        val exists = userDao.getGroupByKey(ConstantsPaths.SAVED_GROUP_KEY)
        if (exists == null) {
            userDao.insertGroup(
                UserGroupEntity(
                    groupKey = ConstantsPaths.SAVED_GROUP_KEY,
                    title = "saved words",
                    icon = "ic_saved_words"
                )
            )
        }
    }

    fun applyPendingDeploy(): Boolean {
        val hasPending = prefs.getBoolean("pending_user_db_deploy", false)
        if (!hasPending) return false

        val pendingFile = File(context.cacheDir, "pending_user_db")

        if (!pendingFile.exists() || pendingFile.length() < 1024) {
            Log.d(TAG_SYNC, "pending deploy: file invalid, skipping")
            prefs.edit { putBoolean("pending_user_db_deploy", false) }
        }

        Log.d(TAG_SYNC, "applying pending deploy, size=${pendingFile.length()}")

        val wal = File(userDbFile.path + "-wal")
        val shm = File(userDbFile.path + "-shm")
        wal.delete()
        shm.delete()

        userDbFile.parentFile?.mkdirs()
        pendingFile.copyTo(userDbFile, overwrite = true)
        pendingFile.delete()

        prefs.edit { putBoolean("pending_user_db_deploy", false) }

        Log.d(TAG_SYNC, "pending deploy applied successfully")
        return true
    }

    private fun validateAndPatchSchema(tempFile: File): Boolean {
        return try {
            val expectedColumns = GlobalDictionaryEntity::class.java.declaredFields
                .mapNotNull { field ->
                    field.getAnnotation(androidx.room.ColumnInfo::class.java)?.name
                        ?: field.name.takeIf { !it.startsWith("$") }
                }
                .toSet()

            val db = android.database.sqlite.SQLiteDatabase.openDatabase(
                tempFile.path, null,
                android.database.sqlite.SQLiteDatabase.OPEN_READWRITE
            )
            val cursor = db.rawQuery("PRAGMA table_info(GlobalDictionary)", null)
            val actualColumns = mutableSetOf<String>()
            while (cursor.moveToNext()) {
                actualColumns.add(cursor.getString(1))
            }
            cursor.close()

            val missingColumns = expectedColumns - actualColumns
            if (missingColumns.isNotEmpty()) {
                Log.w(TAG_SYNC, "Missing columns in downloaded DB: $missingColumns — patching")
                missingColumns.forEach { column ->
                    try {
                        db.execSQL("ALTER TABLE GlobalDictionary ADD COLUMN $column TEXT")
                        Log.d(TAG_SYNC, "Added column: $column")
                    } catch (e: Exception) {
                        Log.e(TAG_SYNC, "Failed to add column $column: $e")
                    }
                }
            } else {
                Log.d(TAG_SYNC, "Schema OK — no missing columns")
            }

            db.close()
            true
        } catch (e: Exception) {
            Log.e(TAG_SYNC, "Schema validation failed: $e")
            false
        }
    }

}
