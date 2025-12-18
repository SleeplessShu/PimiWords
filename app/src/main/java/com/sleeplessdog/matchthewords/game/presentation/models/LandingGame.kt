package com.sleeplessdog.matchthewords.game.presentation.models

data class LandingGame(
    var gameType: GameType = GameType.MATCH8,
    var shouldShow: Boolean = false,
)
