package com.sleeplessdog.pimi.games.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sleeplessdog.matchthewords.R
import com.sleeplessdog.pimi.endGame.EndGameStats
import com.sleeplessdog.pimi.gameSelect.LandingConditions
import com.sleeplessdog.pimi.gameSelect.LandingKeys
import com.sleeplessdog.pimi.games.data.repository.AppPrefs
import com.sleeplessdog.pimi.games.domain.models.WordsController
import com.sleeplessdog.pimi.games.domain.models.WordsGroupsList
import com.sleeplessdog.pimi.games.domain.usecases.GetSelectedGroupsUC
import com.sleeplessdog.pimi.games.domain.usecases.GetWordPairsFromUserGroupUC
import com.sleeplessdog.pimi.games.presentation.controller.LandingPagesController
import com.sleeplessdog.pimi.games.presentation.interfaces.GameEvent
import com.sleeplessdog.pimi.games.presentation.models.GameSettings
import com.sleeplessdog.pimi.games.presentation.models.GameState
import com.sleeplessdog.pimi.games.presentation.models.GameType
import com.sleeplessdog.pimi.games.presentation.models.MatchState
import com.sleeplessdog.pimi.games.presentation.models.SessionStats
import com.sleeplessdog.pimi.games.presentation.models.StatsState
import com.sleeplessdog.pimi.games.presentation.models.Word
import com.sleeplessdog.pimi.games.presentation.parentControllers.ProgressController
import com.sleeplessdog.pimi.score.ProcessGameResultUC
import com.sleeplessdog.pimi.settings.DifficultyLevel
import com.sleeplessdog.pimi.settings.LanguageLevel
import com.sleeplessdog.pimi.utils.GamePrices
import com.sleeplessdog.pimi.utils.GamePrices.DELAY_BEFORE_END_GAME
import com.sleeplessdog.pimi.utils.SupportFunctions
import com.sleeplessdog.pimi.utils.TimeConstants
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class GameViewModel(
    private val wordsController: WordsController,
    private val progressController: ProgressController,
    private val landingManager: LandingPagesController,
    private val getSelectedGroupsUC: GetSelectedGroupsUC,
    private val getWordPairsFromUserGroupUC: GetWordPairsFromUserGroupUC,
    private val processGameResultUC: ProcessGameResultUC,
    private val appPrefs: AppPrefs,
) : ViewModel() {

    private var gameStartTimeMs: Long = 0L

    private val _gameState = MutableLiveData(MatchState())
    val gameState: LiveData<MatchState> = _gameState
    private val _statsState = MutableLiveData(StatsState())
    val statsState: LiveData<StatsState> = _statsState

    private val _navigateToGame = MutableLiveData<GameType?>()
    val navigateToGame: LiveData<GameType?> = _navigateToGame

    private val _gameSettings = MutableLiveData(GameSettings())
    val gameSettings: LiveData<GameSettings> = _gameSettings

    private val _wordsPairs = MutableLiveData<List<Pair<Word, Word>>>()
    val wordsPairs: LiveData<List<Pair<Word, Word>>> = _wordsPairs

    private val _showExitDialogEvent = MutableLiveData<Unit>()
    val showExitDialogEvent: LiveData<Unit> = _showExitDialogEvent

    private val _endGameStats = MutableLiveData<EndGameStats>()
    val endGameStats: LiveData<EndGameStats> = _endGameStats

    private var forcedGroupKey: String? = null
    private var forcedGroupIsUser: Boolean = false

    fun setForcedGroup(key: String?, isUser: Boolean = false) {
        forcedGroupKey = key
        forcedGroupIsUser = isUser
    }

    private var score = 0
    private var lives = 3
    private var difficultLevel = 18
    private val sessionCorrectIds = linkedSetOf<Int>()
    private val sessionWrongIds = linkedSetOf<Int>()
    private var progressSegments: Int = 1
    private var currentStep: Int = 0

    private var allPairs: List<Pair<Word, Word>> = emptyList()

    fun setGame(gameType: GameType) {
        _gameState.value = _gameState.value?.copy(gameType = gameType)
    }

    private suspend fun prepareData() {
        onLoading()

        val selectedGroups = getSelectedGroupsUC()

        /*val enums: Set<WordsGroupsList> = selectedGroups.mapNotNull { key ->
            WordsGroupsList.values().find { it.key == key }
        }.toSet()*/
        val interfaceLang = appPrefs.getUiLanguage()
        val studyLang = appPrefs.getStudyLanguage()
        val difficultLevel = appPrefs.getDifficulty()
        val wordsLevel = appPrefs.getLevels()

        _gameSettings.value = GameSettings(
            language1 = interfaceLang,
            language2 = studyLang,
            difficult = difficultLevel,
            level = wordsLevel,
            category = selectedGroups
        )
    }


    fun onLoading() {
        _gameState.value = _gameState.value?.copy(state = GameState.LOADING)
    }

    fun onGame() {
        viewModelScope.launch {

            prepareData()
            setupGameStats()

            val ok = loadWordsFromDatabase()
            if (!ok) return@launch

            _wordsPairs.value = allPairs
            viewModelScope.launch {
                delay(TimeConstants.LOADING_PROCESS)
                gameStartTimeMs = System.currentTimeMillis()
                _gameState.value = _gameState.value?.copy(state = GameState.GAME)
                landingScreenCheck()
            }
        }
    }

    private fun landingScreenCheck() {
        val gameType = _gameState.value?.gameType ?: GameType.MATCH8
        val gameKey = SupportFunctions.getKeyByGameType(gameType)
        val shouldShow = landingManager.shouldShow(gameKey)
        if (shouldShow) {
            val state = _gameState.value ?: MatchState()
            var landingConditions = LandingConditions()
            when (state.gameType) {
                GameType.MATCH8 -> {
                    landingConditions = LandingConditions(
                        shouldShow = true,
                        headerTextId = R.string.landing_mtw_header,
                        regularTextId = R.string.landing_mtw_text,
                        animation = R.raw.animation_games_pairs_260104,
                        key = LandingKeys.GAME_MTW
                    )
                }

                GameType.TRUEorFALSE -> {
                    landingConditions = LandingConditions(
                        shouldShow = true,
                        headerTextId = R.string.landing_tof_header,
                        regularTextId = R.string.landing_tof_text,
                        animation = R.raw.animation_games_yesno_260104,
                        key = LandingKeys.GAME_TOF
                    )
                }

                GameType.OneOfFour -> {
                    landingConditions = LandingConditions(
                        shouldShow = true,
                        headerTextId = R.string.landing_oof_header,
                        regularTextId = R.string.landing_oof_text,
                        animation = R.raw.animation_games_oneoffour_260104,
                        key = LandingKeys.GAME_OOF
                    )
                }

                GameType.WriteTheWord -> {
                    landingConditions = LandingConditions(
                        shouldShow = true,
                        headerTextId = R.string.landing_wtw_header,
                        regularTextId = R.string.landing_wtw_text,
                        animation = R.raw.animation_games_words_260104,
                        key = LandingKeys.GAME_WTW
                    )
                }
            }
            showLanding(landingConditions)
        }
    }

    private fun showLanding(landingConditions: LandingConditions) {

        _gameState.value = _gameState.value?.copy(landingConditions = landingConditions)
    }

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

    private suspend fun loadWordsFromDatabase(): Boolean {
        val wordsNeeded = when (_gameState.value!!.gameType) {
            GameType.WriteTheWord -> difficultLevel / 6
            GameType.OneOfFour -> difficultLevel * 4
            else -> difficultLevel
        }

        val settings = _gameSettings.value ?: GameSettings()

        val pairs = when {
            forcedGroupKey != null && forcedGroupIsUser -> {
                wordsController.getWordPairs(
                    language1 = settings.language1,
                    language2 = settings.language2,
                    levels = setOf(LanguageLevel.ALL),
                    wordsNeeded = wordsNeeded,
                    categories = setOf(forcedGroupKey!!)
                )
            }

            forcedGroupKey != null -> {
                val category = setOfNotNull(
                    WordsGroupsList.values().find { it.key == forcedGroupKey }.toString()
                )
                wordsController.getWordPairs(
                    language1 = settings.language1,
                    language2 = settings.language2,
                    levels = setOf(LanguageLevel.ALL),
                    wordsNeeded = wordsNeeded,
                    categories = category
                )
            }

            else -> {
                wordsController.getWordPairs(
                    language1 = settings.language1,
                    language2 = settings.language2,
                    levels = settings.level,
                    wordsNeeded = wordsNeeded,
                    categories = settings.category
                )
            }
        }


        if (pairs.isEmpty()) {
            onGameEnd()
            return false
        }

        allPairs = pairs
        return true
    }

    fun onGameEnd() {

        val durationMinutes =
            ((System.currentTimeMillis() - gameStartTimeMs) / 1000 / 60).toInt().coerceAtLeast(1)

        val stats = SessionStats(
            correctIds = sessionCorrectIds.toList(), mistakeIds = sessionWrongIds.toList()
        )
        viewModelScope.launch {
            delay(DELAY_BEFORE_END_GAME)

            processGameResultUC(
                score = score,
                correctIds = stats.correctIds,
                wrongIds = stats.mistakeIds,
                durationMinutes = durationMinutes,
                groupKey = forcedGroupKey,
                isUserGroup = forcedGroupIsUser
            )

            _gameState.value = _gameState.value?.copy(state = GameState.END_OF_GAME)
            _statsState.value = _statsState.value?.copy(
                lives = lives
            )

            _gameState.value = _gameState.value?.copy(state = GameState.END_OF_GAME)
            _endGameStats.value = EndGameStats(
                isWin = lives > 0,
                mistakesCount = sessionWrongIds.size,
                score = score,
                wordsCount = sessionCorrectIds.size,
                sessionPairs = allPairs
            )
        }
    }

    fun restartGame() {
        resetStats()
        onGame()
    }

    private fun setupGameStats() {
        score = 0

        difficultLevel = SupportFunctions.getGameDifficult(
            _gameSettings.value?.difficult ?: DifficultyLevel.MEDIUM
        )

        lives = SupportFunctions.getLivesCount(
            _gameSettings.value?.difficult ?: DifficultyLevel.MEDIUM
        )

        progressSegments = progressController.stepsFor(
            _gameSettings.value?.difficult ?: DifficultyLevel.MEDIUM,
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

    fun navigateToOptions() {
        resetAll()
    }

    fun resetAll() {
        resetStats()
        _gameSettings.value = GameSettings()
        difficultLevel = SupportFunctions.getGameDifficult(DifficultyLevel.MEDIUM)
        lives = SupportFunctions.getLivesCount(DifficultyLevel.MEDIUM)
        _gameState.value = MatchState(state = GameState.GAME)

        progressSegments = progressController.stepsFor(DifficultyLevel.MEDIUM, GameType.MATCH8)
        currentStep = 0
        emitStats()
    }

    fun onLandingShown(showAlways: Boolean, landingKey: LandingKeys) {
        _gameState.value = _gameState.value?.copy(
            landingConditions = LandingConditions(
                shouldShow = false,
                headerTextId = 0,
                regularTextId = 0,
                animation = 0,
                key = landingKey,
            )
        )
        if (!showAlways) landingManager.setShown(landingKey)
    }
}
