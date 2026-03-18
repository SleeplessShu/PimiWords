package com.sleeplessdog.matchthewords.dictionary.trash

/*
class GlobalDatabaseController(private val context: Context) {
    private val storage = Firebase.storage
    private val remoteDbRef =
        storage.reference.child(ConstantsPaths.NETWORK_DATABASE_PATH_ON_FIREBASE)
    private val dbName = ConstantsPaths.GLOBAL_DATABASE_DICTIONARY_NAME
    private val localDbFile: File = context.getDatabasePath(dbName)
    private val prefs =
        context.getSharedPreferences(
            ConstantsPaths.SHARED_PREFS_DATABASE_SETTINGS,
            Context.MODE_PRIVATE
        )

    */
/*suspend fun prepareDatabase(): Result<DatabaseGlobalSource> {

        return try {

            val metadata = remoteDbRef.metadata.await()
            val serverDate = metadata.updatedTimeMillis
            val localDate = prefs.getLong(ConstantsPaths.GLOBAL_DATABASE_DICTIONARY_DATE, 0L)

            if (!localDbFile.exists() || serverDate > localDate) {

                downloadDatabase(serverDate)

                Result.success(DatabaseGlobalSource.NETWORK)

            } else {

                Result.success(DatabaseGlobalSource.NETWORK)
            }

        } catch (e: Exception) {

            if (!localDbFile.exists()) {

                if (copyFromAssets()) {
                    Result.success(DatabaseGlobalSource.ASSETS)
                } else {
                    Result.failure(e)
                }

            } else {
                Result.success(DatabaseGlobalSource.NETWORK)
            }
        }
    }*//*


    */
/**
 * загрузка из локадбного хранилища
 *//*

    private fun copyFromAssets(): Boolean {
        return try {
            localDbFile.parentFile?.mkdirs()
            context.assets.open(ConstantsPaths.ASSETS_DATABASE_DICTIONARY_PATH).use { input ->
                FileOutputStream(localDbFile).use { output ->
                    input.copyTo(output)
                }
            }

            prefs.edit { putLong(ConstantsPaths.GLOBAL_DATABASE_DICTIONARY_DATE, 0L) }
            true
        } catch (e: Exception) {
            Log.d("DEBUG", "copyFromAssets: $e")
            false
        }
    }

    */
/**
 * загрузка из удалённого хранилища
 *//*

    private suspend fun downloadDatabase(newDate: Long) {

        localDbFile.parentFile?.mkdirs()

        val tempFile = File(context.cacheDir, "temp_db")

        remoteDbRef.getFile(tempFile).await()

        if (localDbFile.exists()) {
            localDbFile.delete()
        }

        File(localDbFile.path + "-wal").delete()
        File(localDbFile.path + "-shm").delete()

        tempFile.copyTo(localDbFile, overwrite = true)

        prefs.edit {
            putLong(ConstantsPaths.GLOBAL_DATABASE_DICTIONARY_DATE, newDate)
        }

        tempFile.delete()
    }
}*/
