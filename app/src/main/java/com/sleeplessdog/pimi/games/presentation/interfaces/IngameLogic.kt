package com.sleeplessdog.pimi.games.presentation.interfaces

import androidx.lifecycle.LiveData
import com.sleeplessdog.pimi.games.presentation.models.Word

interface InGameLogic {
    val events: LiveData<GameEvent>
    fun setPool(pairs: List<Pair<Word, Word>>)
}