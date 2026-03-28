package com.sleeplessdog.pimi.database.user

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.sleeplessdog.pimi.dictionary.group_screen.WordUi
import com.sleeplessdog.pimi.settings.Language

@Entity(
    tableName = "UserWords",
    indices = [
        Index("globalId"),
        Index("groupId")
    ],
    foreignKeys = [
        ForeignKey(
            entity = UserGroupEntity::class,
            parentColumns = ["groupKey"],
            childColumns = ["groupId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class UserWordEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val globalId: Long?, // null = user-only слово

    val groupId: String,

    val english: String?,
    val spanish: String?,
    val russian: String?,
    val french: String?,
    val german: String?,
    val armenian: String?,
    val serbian: String?,

    val addedAt: Long = System.currentTimeMillis(),
)

fun UserWordEntity.toUi(
    ui: Language,
    study: Language,
): WordUi {

    fun valueByLanguage(lang: Language): String =
        when (lang) {
            Language.ENGLISH -> english
            Language.SPANISH -> spanish
            Language.RUSSIAN -> russian
            Language.FRENCH -> french
            Language.GERMAN -> german
            Language.ARMENIAN -> armenian
            Language.SERBIAN -> serbian
        } ?: ""

    return WordUi(
        id = id,
        word = valueByLanguage(study),
        translation = valueByLanguage(ui)
    )
}