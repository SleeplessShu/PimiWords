package com.sleeplessdog.matchthewords.game.presentation.models

data class TfQuestion(
    val word: Word,
    val translation: Word,
    val isCorrect: Boolean
)