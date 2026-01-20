package com.sleeplessdog.matchthewords.game.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.sleeplessdog.matchthewords.game.data.WordCategoryEntity

@Database(entities = [WordCategoryEntity::class], version = 1, exportSchema = true)
abstract class AppGroupsDictionary : RoomDatabase() {
    abstract fun wordCategoryDao(): WordCategoryDao

    companion object {
        fun build(context: Context): AppGroupsDictionary =
            Room.databaseBuilder(context, AppGroupsDictionary::class.java, "groups.db")
                .addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)

                        val DEFAULT_ORDER = listOf(
                            "animals", "food", "family", "clothes", "places", "time", "transport",
                            "work", "house", "education", "nature", "emotions", "colors", "numbers",
                            "actions", "objects", "abstract", "entertainment", "communication",
                            "travel", "health", "general_adjectives", "general_adverbs",
                            "general_pronouns", "miscellaneous", "body", "programming",
                            "design", "random"
                        )

                        val defaults = DEFAULT_ORDER.mapIndexed { index, key ->
                            val mapped = KEY_MAP[key] ?: key
                            val icon = mapped
                            seed(
                                key = key,
                                titleKey = "cat_$mapped",
                                iconKey = "ic_category_$icon",
                                order = index
                            )
                        }

                        defaults.forEach {
                            db.execSQL(
                                """
                                INSERT INTO word_categories(`key`, title_key, icon_key, is_selected, is_user, order_in_block)
                                VALUES(?, ?, ?, 0, 0, ?)
                                """.trimIndent(),
                                arrayOf(it.key, it.titleKey, it.iconKey, it.orderInBlock)
                            )
                        }
                        db.execSQL(
                            """
                            INSERT INTO word_categories(`key`, title_key, icon_key, is_selected, is_user, order_in_block)
                            VALUES('saved', 'cat_saved', 'ic_category_saved', 0, 1, -1)
                            """.trimIndent()
                        )
                    }
                })
                .build()

        private val KEY_MAP = mapOf(
            "general_adjectives" to "adjectives",
            "general_adverbs" to "adverbs",
            "general_pronouns" to "pronouns"
        )

        private fun seed(
            key: String, titleKey: String, iconKey: String, order: Int,
        ) = WordCategoryEntity(
            key = key,
            titleKey = titleKey,
            iconKey = iconKey,
            isSelected = false,
            isUser = false,
            orderInBlock = order
        )
    }
}