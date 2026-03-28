package com.sleeplessdog.pimi.games.presentation.models

data class SessionStats(
    val correctIds: List<Int>,
    val mistakeIds: List<Int>
)
