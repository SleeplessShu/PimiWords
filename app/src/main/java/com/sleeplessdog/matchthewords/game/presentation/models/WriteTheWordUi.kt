package com.sleeplessdog.matchthewords.game.presentation.models

data class WriteTheWordUi(
    val prompt: String = "",         // исходное слово (например, EN)
    val target: String = "",         // правильный перевод (например, ES)
    val input: String = "",          // что собрал пользователь
    val letters: List<WriteTheWordLetterUi> = emptyList(),
    val locked: Boolean = false
)