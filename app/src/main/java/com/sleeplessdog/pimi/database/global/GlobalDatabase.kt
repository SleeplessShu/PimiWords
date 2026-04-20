package com.sleeplessdog.pimi.database.global

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.sleeplessdog.pimi.utils.ConstantsPaths.ASSETS_DATABASE_DICTIONARY_PATH
import com.sleeplessdog.pimi.utils.ConstantsPaths.GLOBAL_DATABASE_DICTIONARY_NAME


@Database(
    entities = [GlobalDictionaryEntity::class],
    version = 2,
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
                GLOBAL_DATABASE_DICTIONARY_NAME
            )
                .createFromAsset(ASSETS_DATABASE_DICTIONARY_PATH)
                .build()
    }
}
