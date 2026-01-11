package com.sleeplessdog.matchthewords.game.presentation.fragments

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sleeplessdog.matchthewords.game.data.repositories.AppPrefs
import com.sleeplessdog.matchthewords.game.data.repositories.AuthRepository
import com.sleeplessdog.matchthewords.game.presentation.controller.LandingPagesController
import com.sleeplessdog.matchthewords.game.presentation.controller.UserDatabaseController
import com.sleeplessdog.matchthewords.game.presentation.models.GameType
import com.sleeplessdog.matchthewords.game.presentation.models.LandingKeys
import com.sleeplessdog.matchthewords.game.presentation.models.Language
import kotlinx.coroutines.launch

class GameSelectViewModel(
    private val appPrefs: AppPrefs,
    private val landingManager: LandingPagesController,
    private val userDbController: UserDatabaseController,
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _uiLanguage = MutableLiveData<Language>()
    val uiLanguage: LiveData<Language> = _uiLanguage

    private val _studyLanguage = MutableLiveData<Language>()
    val studyLanguage: LiveData<Language> = _studyLanguage

    private val _availableLanguages = MutableLiveData<List<Language>>()
    val availableLanguages: LiveData<List<Language>> = _availableLanguages

    private val _showLanding = MutableLiveData<Boolean>()
    val showLanding: LiveData<Boolean> = _showLanding

    private val _navigateToGame = MutableLiveData<GameType?>()
    val navigateToGame: LiveData<GameType?> = _navigateToGame

    private val _requestGoogleSignIn = MutableLiveData<Unit>()
    val requestGoogleSignIn: LiveData<Unit> = _requestGoogleSignIn

    init {
        val isFirstRun = landingManager.shouldShow(LandingKeys.APP_FIRST_LAUNCH)
        _showLanding.value = isFirstRun

        if (!authRepository.isUserAuthorized()) {
            _requestGoogleSignIn.value = Unit
        }

        viewModelScope.launch {
            appPrefs.observeStudyLanguage().collect { newLanguage ->
                _studyLanguage.value = newLanguage
            }
        }

        val ui = appPrefs.getUiLanguage()
        rebuild(ui)
        _showLanding.value = isFirstRun
        Log.d("DEBUG", "Первый запуск $isFirstRun: ")
    }

    /**
     * Запускает процесс авторизации через Google. В случае успеха одновляем словарь пользователя.
     */
    fun onGoogleIdTokenReceived(token: String) {
        viewModelScope.launch {
            authRepository.signInWithGoogle(token)
                .onSuccess {
                    beginUpdateUserDictionary()
                }
                .onFailure {
                    Log.e("AUTH", "Auth error", it)
                }
        }
    }

    /**
     * Восстанавливает БД пользовательского словаря из облака.
     */
    fun beginUpdateUserDictionary() {
        viewModelScope.launch {
            userDbController.restoreFromCloud()
        }
    }

    /**
     * Делаем запись в sharedPrefs о том, что первый запуск приложения закончен
     * и лендинг больше показывать не нужно
     */
    fun onLandingShown(showAgain: Boolean) {
        if (!showAgain) {
            landingManager.setShown(LandingKeys.APP_FIRST_LAUNCH)
        }
    }

    fun onLanguagePicked(newStudy: Language) {
        val ui = _uiLanguage.value ?: Language.RUSSIAN
        _studyLanguage.value = newStudy
        appPrefs.save(ui, newStudy)
        rebuild(ui)
    }

    fun onGamePicked(type: GameType) {
        _navigateToGame.value = type
    }

    fun onNavigateConsumed() {
        _navigateToGame.value = null
    }

    private fun rebuild(ui: Language) {
        _availableLanguages.value = Language.entries.filter { it != ui }
    }
}
