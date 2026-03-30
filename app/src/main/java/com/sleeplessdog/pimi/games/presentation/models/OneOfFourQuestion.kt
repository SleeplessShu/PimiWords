package com.sleeplessdog.pimi.games.presentation.models

data class OneOfFourQuestion(
    val originalFirst: Word,          // слово-вопрос (из first)
    val optionsSecond: List<Word>,    // 4 варианта переводов (из second), перемешаны
    val correctSecondId: Int,         // id правильного second
    val consumedFirstIds: Set<Int>    // какие first нужно удалить из пула после ответа
)