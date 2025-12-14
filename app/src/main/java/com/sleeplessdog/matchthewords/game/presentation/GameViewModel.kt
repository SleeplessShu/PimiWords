package com.sleeplessdog.matchthewords.game.presentation

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sleeplessdog.matchthewords.game.data.repositories.AppPrefs
import com.sleeplessdog.matchthewords.game.domain.api.ScoreInteractor
import com.sleeplessdog.matchthewords.game.domain.interactors.WordsController
import com.sleeplessdog.matchthewords.game.domain.models.WordsCategoriesList
import com.sleeplessdog.matchthewords.game.domain.usecase.GetSelectedCategoriesUC
import com.sleeplessdog.matchthewords.game.presentation.interfaces.GameEvent
import com.sleeplessdog.matchthewords.game.presentation.models.DifficultLevel
import com.sleeplessdog.matchthewords.game.presentation.models.GameSettings
import com.sleeplessdog.matchthewords.game.presentation.models.GameState
import com.sleeplessdog.matchthewords.game.presentation.models.GameType
import com.sleeplessdog.matchthewords.game.presentation.models.MatchState
import com.sleeplessdog.matchthewords.game.presentation.models.SessionStats
import com.sleeplessdog.matchthewords.game.presentation.models.StatsState
import com.sleeplessdog.matchthewords.game.presentation.models.Word
import com.sleeplessdog.matchthewords.game.presentation.parentControllers.ProgressController
import com.sleeplessdog.matchthewords.utils.GamePrices
import com.sleeplessdog.matchthewords.utils.SupportFunctions
import com.sleeplessdog.matchthewords.utils.TimeConstants
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class GameViewModel(
    private val wordsController: WordsController,
    private val progressController: ProgressController,
    private val scoreInteractor: ScoreInteractor,
    private val appPrefs: AppPrefs,
    private val getSelectedCategoriesUC: GetSelectedCategoriesUC,
) : ViewModel() {

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

    private val _showExitDialogEvent = MutableLiveData<Unit>()
    val showExitDialogEvent: LiveData<Unit> = _showExitDialogEvent

    // Техническое


    // «игровая экономика»
    private var score = 0
    private var lives = 3
    private var difficultLevel = 18
    private val sessionCorrectIds = linkedSetOf<Int>()
    private val sessionWrongIds = linkedSetOf<Int>()
    private var progressSegments: Int = 1
    private var currentStep: Int = 0

    // кеш загруженных пар
    private var allPairs: List<Pair<Word, Word>> = emptyList()

    init {
        onGame()
    }

    // ------------ Game select ------------
    fun setGame(gameType: GameType) {
        _gameState.value = _gameState.value?.copy(gameType = gameType)
    }

    // ------------ Навигация по экрану ------------


    private suspend fun prepareData() {
        onLoading()

        // 1. Загружаем выбранные категории
        val selectedCategories = getSelectedCategoriesUC()

        val enums: Set<WordsCategoriesList> = selectedCategories.mapNotNull { cat ->
            WordsCategoriesList.values().find { it.key == cat.key }
        }.toSet()

        // 2. Читаем префы
        val interfaceLang = appPrefs.getUiLanguage()
        val studyLang = appPrefs.getStudyLanguage()
        val difficultLevel = appPrefs.getDifficulty()
        val wordsLevel = appPrefs.getLevels()

        // 3. Обновляем настройки одним махом
        _gameSettings.value = GameSettings(
            language1 = interfaceLang,
            language2 = studyLang,
            difficult = difficultLevel,
            level = wordsLevel,
            category = enums
        )

        val settingsTest = _gameSettings.value
        Log.d(
            "DEBUG",
            "prepareData: категории ${settingsTest?.category}, уровень слов ${settingsTest?.level}"
        )
    }


    fun onLoading() {
        _gameState.value = _gameState.value?.copy(state = GameState.LOADING)
    }

    fun onGame() {
        viewModelScope.launch {
            // шаг 1: подготовить настройки (здесь же подтянутся категории из БД)
            prepareData()

            // шаг 2: настроить "экономику" и прогресс
            setupGameStats()

            // шаг 3: загрузить слова
            val ok = loadWordsFromDatabase()
            if (!ok) return@launch

            _wordsPairs.value = allPairs
            viewModelScope.launch {
                delay(TimeConstants.LOADING_PROCESS)
                _gameState.value = _gameState.value?.copy(state = GameState.GAME)
            }
        }
    }

    // ------------ Экономика ------------
    fun reactOnCorrect() {
        addScoreAndLive()
        advanceStepOnCorrect()
        emitStats()
    }

    fun reactOnError() {
        removeScoreAndLive()
        advanceStepOnError()
        emitStats()
    }

    private fun advanceStepOnCorrect() {
        currentStep += 1
    }

    private fun advanceStepOnError() {
        if (progressController.advancesOnWrong(_gameState.value?.gameType ?: GameType.MATCH8)) {
            currentStep += 1
        }
    }

    private fun emitStats() {
        val type = _gameState.value?.gameType ?: GameType.MATCH8
        val p = progressController.progressOf(currentStep, progressSegments, type)

        _statsState.value = _statsState.value?.copy(
            lives = lives,
            score = score.toString(),
            progressSegments = progressSegments,
            progress = p
        ) ?: StatsState(
            lives = lives,
            score = score.toString(),
            todaysScore = "0",
            progressSegments = progressSegments,
            progress = p
        )
    }

    private fun addScoreAndLive() {
        score += GamePrices.ANSWER_PRICE
        if (lives < 3) {
            lives++
        }
    }

    private fun removeScoreAndLive() {
        lives--
        if (lives <= 0) {
            onGameEnd()
        }
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
    private suspend fun loadWordsFromDatabase(): Boolean {
        val wordsNeeded = when (_gameState.value!!.gameType) {
            GameType.WriteTheWord -> difficultLevel / 6
            GameType.OneOfFour -> difficultLevel * 4
            else -> difficultLevel
        }

        val settings = _gameSettings.value ?: GameSettings()
        Log.d("DEBUG", "loadWordsFromDatabase: ${settings.category} ${settings.level}")

        val pairs = wordsController.getWordPairs(
            settings.language1, settings.language2, settings.level, wordsNeeded, settings.category
        )

        if (pairs.isEmpty()) {
            onGameEnd()
            return false
        }

        allPairs = pairs
        return true
    }

    // ------------ Конец игры/сброс ------------
    fun onGameEnd() {
        onLoading()

        val stats = SessionStats(
            correctIds = sessionCorrectIds.toList(), mistakeIds = sessionWrongIds.toList()
        )
        val todaysScore = scoreInteractor.getTodaysResult()
        viewModelScope.launch {
            delay(TimeConstants.LOADING_PROCESS)
            _gameState.value = _gameState.value?.copy(
                state = GameState.END_OF_GAME,
            )
            _statsState.value = _statsState.value?.copy(
                lives = lives, todaysScore = todaysScore.toString()
            )
            wordsController.putRoundStats(stats)
            scoreInteractor.updateTodaysResult(score)
        }
    }

    fun restartGame() {
        resetStats()
        onGame()
    }

    private fun setupGameStats() {
        score = 0

        difficultLevel = SupportFunctions.getGameDifficult(
            _gameSettings.value?.difficult ?: DifficultLevel.MEDIUM
        )

        lives = SupportFunctions.getLivesCount(
            _gameSettings.value?.difficult ?: DifficultLevel.MEDIUM
        )

        progressSegments = progressController.stepsFor(
            _gameSettings.value?.difficult ?: DifficultLevel.MEDIUM,
            _gameState.value?.gameType ?: GameType.MATCH8
        )

        currentStep = 0
        emitStats()
    }

    fun showGameExitQuestion() {
        _showExitDialogEvent.value = Unit
    }

    fun confirmExitGame() {
        resetAll()
    }


    fun resetStats() {
        score = 0

        _wordsPairs.value = emptyList()
        _gameState.value = _gameState.value?.copy(state = GameState.GAME) ?: MatchState(
            state = GameState.LOADING
        )

        currentStep = 0
        emitStats()
    }

    fun resetAll() {
        resetStats()
        _gameSettings.value = GameSettings()
        difficultLevel = SupportFunctions.getGameDifficult(DifficultLevel.MEDIUM)
        lives = SupportFunctions.getLivesCount(DifficultLevel.MEDIUM)
        _gameState.value = MatchState(state = GameState.GAME)

        progressSegments = progressController.stepsFor(DifficultLevel.MEDIUM, GameType.MATCH8)
        currentStep = 0
        emitStats()
    }
}
