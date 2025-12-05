package com.sleeplessdog.matchthewords.game.presentation.models

data class StatsState(
    var lives: Int = 3,
    var score: String = "0",
    var todaysScore: String = "0",
    val progressSegments : Int = 4,
    var progress: Float = 0.0f
)
