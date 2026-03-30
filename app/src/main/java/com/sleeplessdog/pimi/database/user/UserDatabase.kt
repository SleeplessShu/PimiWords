package com.sleeplessdog.pimi.database.user

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.sleeplessdog.pimi.settings.UserSettingsEntity
import com.sleeplessdog.pimi.utils.ConstantsPaths

@Database(
    entities = [
        UserGroupEntity::class,
        UserWordEntity::class,
        UserSettingsEntity::class,
        UserAwardEntity::class,
        AwardProgressEntity::class,
        UserStatsEntity::class,
        WordProgressEntity::class,
        SessionLogEntity::class,
    ],
    version = 2
)
abstract class UserDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun userAwardDao(): UserAwardDao
    abstract fun awardProgressDao(): AwardProgressDao
    abstract fun userStatsDao(): UserStatsDao
    abstract fun wordProgressDao(): WordProgressDao
    abstract fun sessionLogDao(): SessionLogDao

    companion object {
        fun create(context: Context): UserDatabase {
            return Room.databaseBuilder(
                context,
                UserDatabase::class.java,
                ConstantsPaths.USER_DATABASE_DICTIONARY_NAME
            )
                .addMigrations(MIGRATION_1_2)
                .addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        db.execSQL(
                            """
                            INSERT INTO UserGroups (groupKey, title, icon)
                            VALUES ('saved_words', 'saved words', 'ic_saved_words')
                            """.trimIndent()
                        )
                        db.execSQL(
                            """
                            INSERT INTO user_stats (
                                id, totalWordsLearned, totalGamesPlayed,
                                totalScores, weekWordsLearned, weekGamesPlayed, weekScores,
                                weekStartTimestamp, currentStreak, lastPlayedDate, totalSessionMinutes
                            ) VALUES (1, 0, 0, 0, 0, 0, 0, 0, 0, '', 0)
                            """.trimIndent()
                        )
                    }
                })
                .build()
        }
    }
}

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS user_awards (
                awardId TEXT PRIMARY KEY NOT NULL,
                unlocked INTEGER NOT NULL,
                unlockedAt INTEGER
            )
        """
        )
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS award_progress (
                awardId TEXT PRIMARY KEY NOT NULL,
                progress INTEGER NOT NULL,
                target INTEGER NOT NULL
            )
        """
        )
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS user_stats (
                id INTEGER PRIMARY KEY NOT NULL,
                totalWordsLearned INTEGER NOT NULL DEFAULT 0,
                totalGamesPlayed INTEGER NOT NULL DEFAULT 0,
                totalScores INTEGER NOT NULL DEFAULT 0,
                weekWordsLearned INTEGER NOT NULL DEFAULT 0,
                weekGamesPlayed INTEGER NOT NULL DEFAULT 0,
                weekScores INTEGER NOT NULL DEFAULT 0,
                weekStartTimestamp INTEGER NOT NULL DEFAULT 0,
                currentStreak INTEGER NOT NULL DEFAULT 0,
                lastPlayedDate TEXT NOT NULL DEFAULT '',
                totalSessionMinutes INTEGER NOT NULL DEFAULT 0
            )
        """
        )
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS word_progress (
                wordId INTEGER PRIMARY KEY NOT NULL,
                correctCount INTEGER NOT NULL DEFAULT 0,
                wrongCount INTEGER NOT NULL DEFAULT 0,
                lastAnsweredAt INTEGER NOT NULL DEFAULT 0,
                isLearned INTEGER NOT NULL DEFAULT 0,
                groupKey TEXT NOT NULL DEFAULT ''
            )
        """
        )
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS session_log (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                date TEXT NOT NULL,
                durationMinutes INTEGER NOT NULL,
                wordsCount INTEGER NOT NULL,
                isCompleted INTEGER NOT NULL DEFAULT 1
            )
        """
        )

        // удаляем старые таблицы которые больше не используются
        db.execSQL("DROP TABLE IF EXISTS UserWordProgress")
        db.execSQL("DROP TABLE IF EXISTS DailyStats")
        db.execSQL("DROP TABLE IF EXISTS AppStatistics")
    }
}