package com.sleeplessdog.pimi.games.presentation.models

data class WriteTheWordUi(
    val prompt: String = "",
    val target: String = "",
    val input: String = "",
    val letters: List<WriteTheWordLetterUi> = emptyList(),
    val locked: Boolean = false,
    val isCheckCorrect: Boolean = false,
    val isCheckEnabled: Boolean = false,
    val correctAnswer: String = "",
)