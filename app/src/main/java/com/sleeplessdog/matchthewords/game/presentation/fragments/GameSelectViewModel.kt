package com.sleeplessdog.matchthewords.game.presentation.fragments

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sleeplessdog.matchthewords.game.data.repositories.AppPrefs
import com.sleeplessdog.matchthewords.game.presentation.models.GameType
import com.sleeplessdog.matchthewords.game.presentation.models.Language
import kotlinx.coroutines.launch

class GameSelectViewModel(
    private val appPrefs: AppPrefs
) : ViewModel() {

    private val _uiLanguage = MutableLiveData<Language>()
    val uiLanguage: LiveData<Language> = _uiLanguage

    private val _studyLanguage = MutableLiveData<Language>()
    val studyLanguage: LiveData<Language> = _studyLanguage

    private val _availableLanguages = MutableLiveData<List<Language>>()
    val availableLanguages: LiveData<List<Language>> = _availableLanguages

    private val _navigateToGame = MutableLiveData<GameType?>()
    val navigateToGame: LiveData<GameType?> = _navigateToGame

    init {
        viewModelScope.launch {
            viewModelScope.launch {
                appPrefs.observeStudyLanguage().collect { newLanguage ->
                    _studyLanguage.value = newLanguage
                }
            }
        }
        val ui = appPrefs.getUiLanguage()
        val study = appPrefs.getStudyLanguage()
        rebuild(ui, study)
    }

    private fun rebuild(ui: Language, study: Language) {
        _availableLanguages.value = Language.entries.filter { it != ui }
    }

    fun onLanguagePicked(newStudy: Language) {
        val ui = _uiLanguage.value ?: Language.RUSSIAN
        _studyLanguage.value = newStudy
        appPrefs.save(ui, newStudy)
        rebuild(ui, newStudy)
    }

    fun onGamePicked(type: GameType) {
        _navigateToGame.value = type
    }

    fun onNavigateConsumed() {
        _navigateToGame.value = null
    }
}

