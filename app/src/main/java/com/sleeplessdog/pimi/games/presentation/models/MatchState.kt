package com.sleeplessdog.pimi.games.presentation.models

import com.sleeplessdog.pimi.gameSelect.LandingConditions

data class MatchState(
    var gameType: GameType = GameType.MATCH8,
    var state: GameState = GameState.LOADING,
    var landingConditions: LandingConditions = LandingConditions(),
)