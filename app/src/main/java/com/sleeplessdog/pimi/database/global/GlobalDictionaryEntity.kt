package com.sleeplessdog.pimi.database.global

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.sleeplessdog.pimi.dictionary.group_screen.WordUi
import com.sleeplessdog.pimi.settings.Language
import com.sleeplessdog.pimi.settings.LanguageLevel

@Entity(tableName = "GlobalDictionary")
data class GlobalDictionaryEntity(
    @PrimaryKey
    val id: Long,
    val groupKey: String,
    val subGroupKey: String?,
    val difficulty: LanguageLevel,
    val isDeleted: Boolean,
    val english: String,
    val spanish: String?,
    val russian: String?,
    val french: String?,
    val german: String?,
    val armenian: String?,
    val serbian: String?,
)

fun GlobalDictionaryEntity.toUi(
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