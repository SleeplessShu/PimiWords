package com.sleeplessdog.pimi.games.presentation.interfaces

sealed interface GameEvent {
    data class Correct(val wordsIds: List<Int>) : GameEvent
    data class Wrong(val wordsIds: List<Int>)   : GameEvent
    object Completed                    : GameEvent
}