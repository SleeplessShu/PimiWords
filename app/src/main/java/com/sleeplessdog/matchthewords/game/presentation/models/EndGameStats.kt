package com.sleeplessdog.matchthewords.game.presentation.models

data class EndGameStats(
    val isWin: Boolean = false,
    val mistakesCount: Int = 0,
    val score: Int = 0,
    val wordsCount: Int = 0,
)
