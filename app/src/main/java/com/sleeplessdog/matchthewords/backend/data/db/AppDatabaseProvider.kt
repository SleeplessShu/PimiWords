package com.sleeplessdog.matchthewords.backend.data.db

import android.content.Context
import com.sleeplessdog.matchthewords.backend.data.db.global.GlobalDao
import com.sleeplessdog.matchthewords.backend.data.db.global.GlobalDatabase
import com.sleeplessdog.matchthewords.backend.data.db.user.UserDao
import com.sleeplessdog.matchthewords.backend.data.db.user.UserDatabase
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class AppDatabaseProvider(
    private val context: Context,

    ) {
    private val dbuMutex = Mutex()
    private val dbgMutex = Mutex()

    @Volatile
    private var globalDb: GlobalDatabase? = null

    @Volatile
    private var userDb: UserDatabase? = null


    suspend fun <T> withUserDatabaseLock(block: suspend () -> T): T {
        return dbuMutex.withLock {
            block()
        }
    }

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
        return dbuMutex.withLock {
            userDb ?: createUserDatabase().also {
                userDb = it
            }
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


    suspend fun closeGlobalDatabase() {
        globalDb?.let {
            if (it.isOpen) {
                it.close()
            }
        }
        globalDb = null
    }

    suspend fun closeUserDatabase() {
        userDb?.let {
            if (it.isOpen) {
                it.close()
            }
        }
        userDb = null
    }


    suspend fun openGlobalDatabase() {


        if (globalDb == null) {
            globalDb = createGlobalDatabase()
        }

    }

    suspend fun openUserDatabase() {


        if (userDb == null) {
            userDb = createUserDatabase()
        }

    }
}