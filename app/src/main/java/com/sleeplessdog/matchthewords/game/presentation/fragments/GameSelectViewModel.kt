package com.sleeplessdog.matchthewords.game.presentation.fragments

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sleeplessdog.matchthewords.backend.data.repository.AppPrefs
import com.sleeplessdog.matchthewords.game.presentation.controller.LandingPagesController
import com.sleeplessdog.matchthewords.game.presentation.models.GameType
import com.sleeplessdog.matchthewords.game.presentation.models.LandingKeys
import com.sleeplessdog.matchthewords.game.presentation.models.Language
import com.sleeplessdog.matchthewords.utils.LandingRepeatController.ALWAYS_SHOW_FIRST_LANDING
import kotlinx.coroutines.launch

class GameSelectViewModel(
    private val appPrefs: AppPrefs,
    private val landingManager: LandingPagesController,
) : ViewModel() {

    private var forcedGroupKey: String? = null
    private var forcedGroupIsUser: Boolean = false

    data class NavigateToGame(
        val gameType: GameType,
        val groupKey: String?,
        val groupIsUser: Boolean = false,
    )

    private val _uiLanguage = MutableLiveData<Language>()
    val uiLanguage: LiveData<Language> = _uiLanguage

    private val _studyLanguage = MutableLiveData<Language>()
    val studyLanguage: LiveData<Language> = _studyLanguage

    private val _availableLanguages = MutableLiveData<List<Language>>()
    val availableLanguages: LiveData<List<Language>> = _availableLanguages

    private val _showLanding = MutableLiveData<Boolean>()
    val showLanding: LiveData<Boolean> = _showLanding

    private val _navigateToGame = MutableLiveData<NavigateToGame?>()
    val navigateToGame: LiveData<NavigateToGame?> = _navigateToGame

    private val _forcedGroup = MutableLiveData<String?>(forcedGroupKey)
    val forcedGroup: LiveData<String?> = _forcedGroup

    init {
        /**
         * проверяем необходимость показывать лэндинг
         */
        val isFirstRun = landingManager.shouldShow(LandingKeys.APP_FIRST_LAUNCH)
        Log.d("DEBUG", "Первый запуск $isFirstRun: ")
        _showLanding.value = isFirstRun

        viewModelScope.launch {
            appPrefs.observeStudyLanguage().collect { newLanguage ->
                _studyLanguage.value = newLanguage
            }
        }

        val ui = appPrefs.getUiLanguage()
        rebuild(ui)

        Log.d("DEBUG", "Первый запуск $isFirstRun: ")
    }

    fun setForcedGroup(key: String?, isUser: Boolean = false) {
        forcedGroupKey = key
        forcedGroupIsUser = isUser
    }

    fun onLandingShown() {
        _showLanding.value = false
        if (!ALWAYS_SHOW_FIRST_LANDING) {
            landingManager.setShown(LandingKeys.APP_FIRST_LAUNCH)
            Log.d("DEBUG", "setLandingShown")
        }
    }

    fun onLanguagePicked(newStudy: Language) {
        val ui = _uiLanguage.value ?: Language.RUSSIAN
        _studyLanguage.value = newStudy
        appPrefs.save(ui, newStudy)
        rebuild(ui)
    }

    fun onGamePicked(type: GameType) {
        _navigateToGame.value = NavigateToGame(
            gameType = type,
            groupKey = forcedGroupKey,
            groupIsUser = forcedGroupIsUser
        )
    }

    fun onNavigateConsumed() {
        _navigateToGame.value = null
    }

    private fun rebuild(ui: Language) {
        _availableLanguages.value = Language.entries.filter { it != ui }
    }
}
