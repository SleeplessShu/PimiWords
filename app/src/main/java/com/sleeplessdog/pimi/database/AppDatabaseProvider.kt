package com.sleeplessdog.pimi.database

import android.content.Context
import com.sleeplessdog.pimi.database.global.GlobalDao
import com.sleeplessdog.pimi.database.global.GlobalDatabase
import com.sleeplessdog.pimi.database.user.UserDao
import com.sleeplessdog.pimi.database.user.UserDatabase
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class AppDatabaseProvider(
    private val context: Context,

    ) {
    private val dbuMutex = Mutex()
    private val dbgMutex = Mutex()

    @Volatile
    private var isUserDbLocked = false

    @Volatile
    private var globalDb: GlobalDatabase? = null

    @Volatile
    private var userDb: UserDatabase? = null

    suspend fun <T> withGlobalDatabaseLock(block: suspend () -> T): T {
        return dbgMutex.withLock {
            block()
        }
    }

    suspend fun getGlobalDatabase(): GlobalDatabase {
        return dbgMutex.withLock {
            globalDb ?: createGlobalDatabase().also {
                globalDb = it
            }
        }
    }

    suspend fun getUserDatabase(): UserDatabase {
        while (isUserDbLocked) {
            kotlinx.coroutines.delay(50)
        }
        return dbuMutex.withLock {
            userDb ?: createUserDatabase().also { userDb = it }
        }
    }

    fun getGlobalDao(): GlobalDao {

        if (globalDb == null) {
            globalDb = GlobalDatabase.create(context)
        }
        return globalDb!!.globalDao()
    }

    fun getUserDao(): UserDao {

        if (userDb == null) {
            userDb = UserDatabase.create(context)

        }
        return userDb!!.userDao()
    }


    private fun createGlobalDatabase(): GlobalDatabase {

        return GlobalDatabase.create(context)

    }

    private fun createUserDatabase(): UserDatabase {

        return UserDatabase.create(context)

    }


    fun closeGlobalDatabase() {

        globalDb?.let {
            if (it.isOpen) {
                it.close()
            }
        }
        globalDb = null
    }


    fun openGlobalDatabase() {

        if (globalDb == null) {
            globalDb = createGlobalDatabase()
        }
    }

    fun openUserDatabase() {

        if (userDb == null) {
            userDb = createUserDatabase()
        }
    }
}