package com.sleeplessdog.matchthewords.game.presentation.models

import com.sleeplessdog.matchthewords.utils.ConstantsConditions.ONE_OF_FOUR_SET

data class GameUiOOF(
    val originalText: String = "",
    val options: List<String> = listOf("", "", "", ""),
    val states: List<ButtonState> = List(ONE_OF_FOUR_SET) { ButtonState.DEFAULT },
    val locked: Boolean = false
)
