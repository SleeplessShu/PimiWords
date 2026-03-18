package com.sleeplessdog.matchthewords.backend.data.db.user

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.sleeplessdog.matchthewords.backend.domain.models.AppStatisticsEntity
import com.sleeplessdog.matchthewords.backend.domain.models.DailyStatsEntity
import com.sleeplessdog.matchthewords.backend.domain.models.UserSettingsEntity
import com.sleeplessdog.matchthewords.utils.ConstantsPaths.USER_DATABASE_DICTIONARY_NAME

@Database(
    entities = [
        UserGroupEntity::class,
        UserWordEntity::class,
        UserWordProgressEntity::class,
        AppStatisticsEntity::class,
        UserSettingsEntity::class,
        DailyStatsEntity::class,
    ],
    version = 1,
    exportSchema = false
)
abstract class UserDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao

    companion object {

        fun create(context: Context): UserDatabase =
            Room.databaseBuilder(
                context,
                UserDatabase::class.java,
                USER_DATABASE_DICTIONARY_NAME
            )
                .addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)

                        // дефолтная группа "saved words"
                        db.execSQL(
                            """
                            INSERT INTO UserGroups (groupKey, title, icon)
                            VALUES ('saved_words', 'saved words', 'ic_saved_words')
                            """.trimIndent()
                        )
                    }
                })
                .build()
    }
}
