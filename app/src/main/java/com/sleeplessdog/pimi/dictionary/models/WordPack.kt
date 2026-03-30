package com.sleeplessdog.pimi.dictionary.models

import com.sleeplessdog.pimi.settings.Language

data class WordPack(
    val id: String = "",
    val engName: String? = null,
    val rusName: String? = null,
    val serName: String? = null,
    val spaName: String? = null,
    val freName: String? = null,
    val gerName: String? = null,
    val armName: String? = null,
    val wordsCount: Int = 0,
    val words: List<WordPackEntry> = emptyList(),
) {
    fun nameForLanguage(language: Language): String {
        return when (language) {
            Language.RUSSIAN -> rusName
            Language.ENGLISH -> engName
            Language.SERBIAN -> serName
            Language.SPANISH -> spaName
            Language.FRENCH -> freName
            Language.GERMAN -> gerName
            Language.ARMENIAN -> armName
        } ?: ""
    }
}

data class WordPackEntry(
    val english: String? = null,
    val russian: String? = null,
    val serbian: String? = null,
    val spanish: String? = null,
    val french: String? = null,
    val german: String? = null,
    val armenian: String? = null,
)

data class WordPackMeta(
    val engName: String? = null,
    val rusName: String? = null,
    val serName: String? = null,
    val spaName: String? = null,
    val freName: String? = null,
    val gerName: String? = null,
    val armName: String? = null,
    val wordsCount: Int = 0,
    val fileName: String = "",
) {
    fun nameForLanguage(language: Language): String {
        return when (language) {
            Language.RUSSIAN -> rusName
            Language.ENGLISH -> engName
            Language.SERBIAN -> serName
            Language.SPANISH -> spaName
            Language.FRENCH -> freName
            Language.GERMAN -> gerName
            Language.ARMENIAN -> armName
        } ?: engName ?: fileName
    }
}

data class WordPackUi(
    val name: String,
    val wordsCount: Int = 0,
    val fileName: String = "",
)

object WordPackUiMapper {
    fun map(pack: WordPackMeta, uiLanguage: Language): WordPackUi {
        return WordPackUi(
            name = pack.nameForLanguage(uiLanguage),
            wordsCount = pack.wordsCount,
            fileName = pack.fileName,
        )
    }
}