package com.sleeplessdog.pimi.dictionary.word_packs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sleeplessdog.pimi.dictionary.models.WordPackUi
import com.sleeplessdog.pimi.dictionary.models.WordPackUiMapper
import com.sleeplessdog.pimi.games.data.repository.AppPrefs
import com.sleeplessdog.pimi.settings.Language
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class WordPacksViewModel(
    private val getWordPacks: GetWordPacksUC,
    private val installWordPack: InstallWordPackUC,
    private val appPrefs: AppPrefs,
) : ViewModel() {

    sealed class State {
        object Idle : State()
        object Loading : State()
        data class Loaded(val packs: List<WordPackUi>) : State()
        data class Error(val message: String) : State()
    }

    sealed class InstallState {
        object Idle : InstallState()
        object Installing : InstallState()
        data class Success(val packName: String) : InstallState()
        data class Error(val message: String) : InstallState()
    }

    val uiLanguage: Language get() = appPrefs.getUiLanguage()

    private val _state = MutableStateFlow<State>(State.Idle)
    val state: StateFlow<State> = _state

    private val _installState = MutableStateFlow<InstallState>(InstallState.Idle)
    val installState: StateFlow<InstallState> = _installState

    fun loadPacks() {
        viewModelScope.launch {
            _state.value = State.Loading
            getWordPacks().onSuccess {
                _state.value = State.Loaded(it.map { WordPackUiMapper.map(it, uiLanguage) })
            }.onFailure { _state.value = State.Error(it.message ?: "Ошибка") }
        }
    }

    fun install(fileName: String) {
        viewModelScope.launch {
            _installState.value = InstallState.Installing
            installWordPack(fileName).onSuccess {
                _installState.value = InstallState.Success(fileName)
            }.onFailure {
                _installState.value = InstallState.Error(it.message ?: "Ошибка")
            }
        }
    }

    fun resetInstallState() {
        _installState.value = InstallState.Idle
    }
}