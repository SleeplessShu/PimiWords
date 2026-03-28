package com.sleeplessdog.pimi.games.presentation.models

data class TfQuestionUi(
    val word: Word,
    val shownTranslation: Word,
    val isCorrect: Boolean,
    val locked: Boolean = false
)