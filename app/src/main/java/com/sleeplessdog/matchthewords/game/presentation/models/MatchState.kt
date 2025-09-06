package com.sleeplessdog.matchthewords.game.presentation.models

data class MatchState (
    var gameType: GameType = GameType.MATCH8,
    var state: GameState = GameState.MATCH_SETTINGS,
    var lives: Int = 3,
    var score: String = "00000",
    var todaysScore: String = "00000",
    var answerPointsState: AnswerPointsState = AnswerPointsState.EMPTY
)