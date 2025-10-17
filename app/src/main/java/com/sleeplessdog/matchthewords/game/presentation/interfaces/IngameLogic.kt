package com.sleeplessdog.matchthewords.game.presentation.interfaces

import androidx.lifecycle.LiveData
import com.sleeplessdog.matchthewords.game.presentation.models.Word

interface InGameLogic {
    val events: LiveData<GameEvent>
    fun setPool(pairs: List<Pair<Word, Word>>)
}