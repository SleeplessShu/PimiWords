package com.sleeplessdog.matchthewords.game.presentation.models

data class EndGameActionStatus(
    var isVisible: Boolean = false,
    var action: EndGameWordsAction = EndGameWordsAction.SAVE_WORDS_TO_USERS_DICTIONARY,
)
