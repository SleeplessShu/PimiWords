package com.sleeplessdog.pimi.dictionary.group_screen


enum class GroupType {
    USER,
    GLOBAL
}


data class GroupUiState(
    val title: String = "",
    val words: List<WordUi> = emptyList(),
    val loading: Boolean = true,
)

data class WordUi(
    val id: Long,
    val word: String,
    val translation: String,
)

/**
 * стейты для отображения содержимого групп главного экрана словаря
 */
enum class DictionaryWordGroups {
    MIXED,
    USERS,
    GLOBAL
}

