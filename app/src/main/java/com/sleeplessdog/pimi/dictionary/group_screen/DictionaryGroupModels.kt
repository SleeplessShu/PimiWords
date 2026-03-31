package com.sleeplessdog.pimi.dictionary.group_screen


enum class GroupType {
    USER,
    GLOBAL
}

data class WordUi(
    val id: Long,
    val word: String,
    val translation: String,
)