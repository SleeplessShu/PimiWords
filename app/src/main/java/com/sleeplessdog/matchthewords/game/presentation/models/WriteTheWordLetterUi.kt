package com.sleeplessdog.matchthewords.game.presentation.models

data class WriteTheWordLetterUi(
    val id: Int,
    val char: Char,
    val used: Boolean = false
)