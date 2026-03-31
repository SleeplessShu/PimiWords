package com.sleeplessdog.pimi.games.presentation.models

data class OneOfFourQuestion(
    val originalFirst: Word,
    val optionsSecond: List<Word>,
    val correctSecondId: Int,
    val consumedFirstIds: Set<Int>
)