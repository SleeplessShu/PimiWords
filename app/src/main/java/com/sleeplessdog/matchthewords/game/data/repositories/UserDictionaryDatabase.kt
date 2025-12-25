package com.sleeplessdog.matchthewords.game.data.repositories

import androidx.room.Database
import androidx.room.RoomDatabase
import com.sleeplessdog.matchthewords.game.data.UserWordEntity
import com.sleeplessdog.matchthewords.game.data.database.UserDictionaryDao

@Database(entities = [UserWordEntity::class], version = 1, exportSchema = false)
abstract class UserDictionaryDatabase : RoomDatabase() {

    abstract fun userDictionaryDao(): UserDictionaryDao
}