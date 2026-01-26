package com.sleeplessdog.matchthewords.backend.data.db.global

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters


@Database(
    entities = [GlobalDictionaryEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(GlobalDbConverters::class)
abstract class GlobalDatabase : RoomDatabase() {

    abstract fun globalDao(): GlobalDao

    companion object {
        fun create(context: Context): GlobalDatabase =
            Room.databaseBuilder(
                context,
                GlobalDatabase::class.java,
                "global_dictionary.db"
            )
                .fallbackToDestructiveMigration()
                .build()
    }
}
