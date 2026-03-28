package com.sleeplessdog.pimi.gameSelect

import com.sleeplessdog.pimi.games.presentation.models.GameType

data class LandingGame(
    var gameType: GameType = GameType.MATCH8,
    var shouldShow: Boolean = false,
)
