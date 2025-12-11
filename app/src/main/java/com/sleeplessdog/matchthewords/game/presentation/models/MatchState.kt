package com.sleeplessdog.matchthewords.game.presentation.models

data class MatchState (
    var gameType: GameType = GameType.MATCH8,
    var state: GameState = GameState.LOADING,
    val settings: GameSettings? = null
)
