package com.sleeplessdog.matchthewords.game.presentation.models

data class SessionStats(
    val correctIds: List<Int>,
    val mistakeIds: List<Int>
)
