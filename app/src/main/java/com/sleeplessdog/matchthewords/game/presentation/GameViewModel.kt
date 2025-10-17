package com.sleeplessdog.matchthewords.game.presentation

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sleeplessdog.matchthewords.game.domain.api.GameInteractor
import com.sleeplessdog.matchthewords.game.domain.api.ScoreInteractor
import com.sleeplessdog.matchthewords.game.domain.models.LanguageLevel
import com.sleeplessdog.matchthewords.game.domain.models.WordCategory
import com.sleeplessdog.matchthewords.game.presentation.interfaces.GameEvent
import com.sleeplessdog.matchthewords.game.presentation.models.DifficultLevel
import com.sleeplessdog.matchthewords.game.presentation.models.GameSettings
import com.sleeplessdog.matchthewords.game.presentation.models.GameState
import com.sleeplessdog.matchthewords.game.presentation.models.GameType
import com.sleeplessdog.matchthewords.game.presentation.models.Language
import com.sleeplessdog.matchthewords.game.presentation.models.MatchState
import com.sleeplessdog.matchthewords.game.presentation.models.SessionStats
import com.sleeplessdog.matchthewords.game.presentation.models.StatsState
import com.sleeplessdog.matchthewords.game.presentation.models.Word
import com.sleeplessdog.matchthewords.utils.GamePrices
import com.sleeplessdog.matchthewords.utils.SupportFunctions
import com.sleeplessdog.matchthewords.utils.TimeReactionConstants
import kotlinx.coroutines.launch

class GameViewModel(
    private val gameInteractor: GameInteractor,
    private val supportFunctions: SupportFunctions,
    private val scoreInteractor: ScoreInteractor,
) : ViewModel() {

    private val languages = Language.entries.toTypedArray()
    private val levels = LanguageLevel.entries.toTypedArray()
    private val categories = WordCategory.entries.toTypedArray()
    private val difficult = DifficultLevel.entries.toTypedArray()

    // Верхнее состояние экрана игры
    private val _gameState = MutableLiveData(MatchState())
    val gameState: LiveData<MatchState> = _gameState

    private val _statsState = MutableLiveData(StatsState())
    val statsState: LiveData<StatsState> = _statsState

    // Настройки матча
    private val _gameSettings = MutableLiveData(GameSettings())
    val gameSettings: LiveData<GameSettings> = _gameSettings

    // Пул пар для конкретного раунда/страницы — его потребляют ВСЕ дочерние VM
    private val _wordsPairs = MutableLiveData<List<Pair<Word, Word>>>()
    val wordsPairs: LiveData<List<Pair<Word, Word>>> = _wordsPairs

    // Техническое
    private val handler = Handler(Looper.getMainLooper())

    // «игровая экономика»
    private var score = 0
    private var lives = 3
    private var difficultLevel = 18
    private val sessionCorrectIds = linkedSetOf<Int>()
    private val sessionWrongIds   = linkedSetOf<Int>()

    // кеш загруженных пар
    private var allPairs: List<Pair<Word, Word>> = emptyList()

    init {
        onMatchSettings()
    }

    // ------------ Game select ------------
    fun setGame(gameType: GameType) {
        _gameState.value = _gameState.value?.copy(gameType = gameType)
    }


    // ------------ Match settings ------------
    fun switchLanguage1(isNext: Boolean) {
        val nextLanguage =
            supportFunctions.switchItem(_gameSettings.value?.language1, languages, isNext)
        if (nextLanguage == _gameSettings.value?.language2) {
            val adjustedLanguage = supportFunctions.switchItem(nextLanguage, languages, isNext)
            updateLanguage1(adjustedLanguage)
        } else {
            updateLanguage1(nextLanguage)
        }
    }

    fun switchLanguage2(isNext: Boolean) {
        val nextLanguage =
            supportFunctions.switchItem(_gameSettings.value?.language2, languages, isNext)
        if (nextLanguage == _gameSettings.value?.language1) {
            val adjustedLanguage = supportFunctions.switchItem(nextLanguage, languages, isNext)
            updateLanguage2(adjustedLanguage)
        } else {
            updateLanguage2(nextLanguage)
        }
    }

    fun switchWordsLevel(isNext: Boolean) {
        val nextLevel = supportFunctions.switchItem(_gameSettings.value?.level, levels, isNext)
        updateLevel(nextLevel)
    }

    fun switchDifficultLevel(isNext: Boolean) {
        val nextDifficult =
            supportFunctions.switchItem(_gameSettings.value?.difficult, difficult, isNext)
        updateDifficult(nextDifficult)
    }

    fun switchWordsCategory(isNext: Boolean) {
        val nextCategory =
            supportFunctions.switchItem(_gameSettings.value?.category, categories, isNext)
        updateCategory(nextCategory)
    }

    fun updateLanguage1(newLanguage: Language) {
        _gameSettings.value = _gameSettings.value?.copy(language1 = newLanguage)
    }

    fun updateLanguage2(newLanguage: Language) {
        _gameSettings.value = _gameSettings.value?.copy(language2 = newLanguage)
    }

    fun updateLevel(newLevel: LanguageLevel) {
        _gameSettings.value = _gameSettings.value?.copy(level = newLevel)
    }

    fun updateCategory(newCategory: WordCategory) {
        _gameSettings.value = _gameSettings.value?.copy(category = newCategory)
    }

    fun updateDifficult(newDifficult: DifficultLevel) {
        _gameSettings.value = _gameSettings.value?.copy(difficult = newDifficult)
    }

    // ------------ Навигация по экрану ------------
    fun onMatchSettings() {
        _gameState.value = _gameState.value?.copy(state = GameState.MATCH_SETTINGS)
    }

    fun onLoading() {
        _gameState.value = _gameState.value?.copy(state = GameState.LOADING)
    }

    fun onGame() {
        onLoading()
        setupScoreLivesDifficult()
        loadWordsFromDatabase {
            _wordsPairs.value = allPairs
            handler.postDelayed({
                _gameState.value = _gameState.value?.copy(state = GameState.GAME)
            }, TimeReactionConstants.LOADING)
        }
    }

    // ------------ Экономика ------------
    fun reactOnError() {
        removeScoreAndLive()
        _statsState.postValue(
            _statsState.value?.copy(
                lives = lives, score = supportFunctions.getScoreAsString(score)
            )
        )
    }

    fun reactOnCorrect() {
        addScoreAndLive()
        _statsState.postValue(
            _statsState.value?.copy(
                lives = lives, score = supportFunctions.getScoreAsString(score)
            )
        )
    }


    private fun addScoreAndLive() {
        score += GamePrices.ANSWER_PRICE
        if (lives < 3 && _gameSettings.value?.difficult != DifficultLevel.SURVIVAL) {
            lives++
        }
    }

    private fun removeScoreAndLive() {
        lives--
        score -= GamePrices.MISTAKE_PRICE
    }

    fun onGameEvent(ev: GameEvent) {
        when (ev) {
            is GameEvent.Correct -> {
                sessionCorrectIds.addAll(ev.wordsIds)
                reactOnCorrect()
            }
            is GameEvent.Wrong -> {
               sessionWrongIds.addAll(ev.wordsIds)
                reactOnError()
            }
            GameEvent.Completed -> onGameEnd()
        }
    }

    // ------------ Загрузка пар ------------
    private fun loadWordsFromDatabase(onSuccess: () -> Unit) {
        viewModelScope.launch {
            val pairs = gameInteractor.getWordPairs(
                _gameSettings.value?.language1 ?: Language.ENGLISH,
                _gameSettings.value?.language2 ?: Language.SPANISH,
                _gameSettings.value?.level ?: LanguageLevel.A1,
                difficultLevel,
                _gameSettings.value?.category ?: WordCategory.RANDOM
            )
            if (pairs.isEmpty()) { onGameEnd(); return@launch }
            allPairs = pairs
            onSuccess()
        }
    }

    // ------------ Конец игры/сброс ------------
    fun onGameEnd() {
        onLoading()

        val stats = SessionStats(
            correctIds = sessionCorrectIds.toList(),
            mistakeIds = sessionWrongIds.toList()
        )
        val todaysScore = scoreInteractor.getTodaysResult()
        handler.postDelayed({
            _gameState.value = _gameState.value?.copy(
                state = GameState.END_OF_GAME,
            )
            _statsState.value = _statsState.value?.copy(
                lives = lives,
                todaysScore = supportFunctions.getScoreAsString(todaysScore)
            )
            gameInteractor.putRoundStats(stats)
            scoreInteractor.updateTodaysResult(score)
        }, TimeReactionConstants.LOADING)
    }

    fun restartGame() {
        resetStats()
        onGame()
    }

    private fun setupScoreLivesDifficult() {
        score = 0
        difficultLevel = supportFunctions.getGameDifficult(
            _gameSettings.value?.difficult ?: DifficultLevel.MEDIUM
        )
        lives = supportFunctions.getLivesCount(
            _gameSettings.value?.difficult ?: DifficultLevel.MEDIUM
        )
    }

    fun resetStats() {
        Log.d("DEBUG", "resetStats: !!!")
        handler.removeCallbacksAndMessages(null)
        score = 0
        // возвращаемся в настройки
        _wordsPairs.value = emptyList()
        _gameState.value = _gameState.value?.copy(
            state = GameState.MATCH_SETTINGS
        ) ?: MatchState(
            state = GameState.MATCH_SETTINGS,
        )
        _statsState.value = _statsState.value?.copy(
            lives = lives,
            score = supportFunctions.getScoreAsString(score)
        )
    }

    fun resetAll() {
        resetStats()
        _gameSettings.value = GameSettings()
        val defLives = supportFunctions.getLivesCount(DifficultLevel.MEDIUM)
        difficultLevel = supportFunctions.getGameDifficult(DifficultLevel.MEDIUM)
        lives = defLives
        _gameState.value = MatchState(
            state = GameState.MATCH_SETTINGS
        )
        _statsState.value = _statsState.value?.copy(
            lives = defLives,
            score = supportFunctions.getScoreAsString(0)
        )
    }
}
