package com.sleeplessdog.matchthewords.game.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sleeplessdog.matchthewords.game.presentation.controller.GameLevelLoader
import com.sleeplessdog.matchthewords.game.presentation.controller.GameSessionController
import com.sleeplessdog.matchthewords.game.presentation.interfaces.GameEvent
import com.sleeplessdog.matchthewords.game.presentation.models.GameState
import com.sleeplessdog.matchthewords.game.presentation.models.GameType
import com.sleeplessdog.matchthewords.game.presentation.models.MatchState
import com.sleeplessdog.matchthewords.game.presentation.models.StatsState
import com.sleeplessdog.matchthewords.game.presentation.models.Word
import com.sleeplessdog.matchthewords.utils.ConstantsTimeReaction
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class GameViewModel(
    private val levelLoader: GameLevelLoader,
    private val session: GameSessionController
) : ViewModel() {

    private val _gameState = MutableLiveData(MatchState())
    val gameState: LiveData<MatchState> = _gameState

    private val _statsState = MutableLiveData(StatsState())
    val statsState: LiveData<StatsState> = _statsState

    private val _wordsPairs = MutableLiveData<List<Pair<Word, Word>>>()
    val wordsPairs: LiveData<List<Pair<Word, Word>>> = _wordsPairs

    private val _showExitDialogEvent = MutableLiveData<Unit>()
    val showExitDialogEvent: LiveData<Unit> = _showExitDialogEvent

    init {
        onGame()
    }

    fun setGame(gameType: GameType) {
        _gameState.value = _gameState.value?.copy(gameType = gameType)
    }

    fun onGame() {
        setLoadingState()
        viewModelScope.launch {
            val type = _gameState.value?.gameType ?: GameType.MATCH8

            val data = levelLoader.loadLevel(type)
            if (data == null) {
                onGameEnd()
                return@launch
            }
            _wordsPairs.value = data.words

            session.setup(data.settings, type)
            updateStatsUI()

            delay(ConstantsTimeReaction.LOADING)
            _gameState.value = _gameState.value?.copy(
                state = GameState.GAME,
                settings = data.settings)
        }
    }

    fun onGameEvent(ev: GameEvent) {
        val type = _gameState.value?.gameType ?: GameType.MATCH8
        when (ev) {
            is GameEvent.Correct -> {
                session.onCorrect(ev.wordsIds)
                updateStatsUI()
            }
            is GameEvent.Wrong -> {
                val isDead = session.onWrong(ev.wordsIds, type)
                updateStatsUI()
                if (isDead) onGameEnd()
            }
            GameEvent.Completed -> onGameEnd()
        }
    }

    fun onGameEnd() {
        setLoadingState()
        viewModelScope.launch {

            val todaysBest = session.saveResults()

            delay(ConstantsTimeReaction.LOADING)
            _gameState.value = _gameState.value?.copy(state = GameState.END_OF_GAME)

            updateStatsUI(todaysBest.toString())
        }
    }

    fun restartGame() {
        session.resetForRestart()
        _wordsPairs.value = emptyList()
        updateStatsUI()
        onGame()
    }

    fun resetStats() {
        session.resetForRestart()
        updateStatsUI()
    }

    fun showGameExitQuestion() {
        _showExitDialogEvent.value = Unit
    }

    private fun updateStatsUI(todaysScore: String? = null) {
        val type = _gameState.value?.gameType ?: GameType.MATCH8
        val scoreToShow = todaysScore ?: _statsState.value?.todaysScore.toString()

        _statsState.value = session.getStatsState(type, scoreToShow)
    }

    private fun setLoadingState() {
        _gameState.value = _gameState.value?.copy(state = GameState.LOADING)
    }
}
