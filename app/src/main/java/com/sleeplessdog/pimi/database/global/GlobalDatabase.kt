package com.sleeplessdog.pimi.database.global

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.sleeplessdog.pimi.utils.ConstantsPaths.ASSETS_DATABASE_DICTIONARY_PATH
import com.sleeplessdog.pimi.utils.ConstantsPaths.GLOBAL_DATABASE_DICTIONARY_NAME


@Database(
    entities = [GlobalDictionaryEntity::class], version = 2, exportSchema = false
)
@TypeConverters(GlobalDbConverters::class)
abstract class GlobalDatabase : RoomDatabase() {

    abstract fun globalDao(): GlobalDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "ALTER TABLE GlobalDictionary ADD COLUMN armTranslit TEXT"
                )
            }
        }

        fun create(context: Context): GlobalDatabase = Room.databaseBuilder(
            context, GlobalDatabase::class.java, GLOBAL_DATABASE_DICTIONARY_NAME
        ).createFromAsset(
            ASSETS_DATABASE_DICTIONARY_PATH,
            object : RoomDatabase.PrepackagedDatabaseCallback() {
                override fun onOpenPrepackagedDatabase(db: SupportSQLiteDatabase) {
                    try {
                        db.execSQL("ALTER TABLE GlobalDictionary ADD COLUMN armTranslit TEXT")
                    } catch (e: Exception) {
                        Log.d(
                            "GlobalDatabase setup",
                            "onOpenPrepackagedDatabase: armTranslit column already exists"
                        )
                    }
                }
            }).addMigrations(MIGRATION_1_2).build()
    }
}
