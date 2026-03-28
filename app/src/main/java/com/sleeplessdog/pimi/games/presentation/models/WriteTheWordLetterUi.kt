package com.sleeplessdog.pimi.games.presentation.models

data class WriteTheWordLetterUi(
    val id: Int,
    val char: Char,
    val used: Boolean = false
)