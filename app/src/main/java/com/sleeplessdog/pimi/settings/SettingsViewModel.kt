package com.sleeplessdog.pimi.settings

import android.app.Application
import android.content.Context
import android.widget.Toast
import androidx.core.content.ContextCompat.getString
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sleeplessdog.pimi.R
import com.sleeplessdog.pimi.dictionary.models.GroupSettingsUiMapper
import com.sleeplessdog.pimi.games.data.repository.AppPrefs
import com.sleeplessdog.pimi.games.domain.models.CombinedGroupsSettingsUi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Locale

class SettingsViewModel(

    observeAllGroups: ObserveAllGroupsForSettingsUC,
    private val toggleUC: SettingsToggleCategoryUC,
    private val saveSelectionUC: SettingsSaveSelectionUC,
    private val saveLevelsUC: SettingsSaveLevelsUC,
    private val observeLevelsUC: SettingsObserveLevelsUC,
    private val groupSettingsUiMapper: GroupSettingsUiMapper,
    private val app: Application,
    private val appPrefs: AppPrefs,
) : ViewModel() {
    private val _uiLanguage = MutableLiveData<Language>()
    val uiLanguage: LiveData<Language> = _uiLanguage

    private val _studyLanguage = MutableLiveData<Language>()
    val studyLanguage: LiveData<Language> = _studyLanguage

    private val _uiLanguageList = MutableLiveData<List<Language>>()
    val uiLanguageList: LiveData<List<Language>> = _uiLanguageList

    private val _studyLanguageList = MutableLiveData<List<Language>>()
    val studyLanguageList: LiveData<List<Language>> = _studyLanguageList

    private val _restartActivity = MutableLiveData<Unit>()
    val restartActivity: LiveData<Unit> = _restartActivity

    private val _levels = MutableLiveData<Set<LanguageLevel>>()
    val levels: LiveData<Set<LanguageLevel>> = _levels

    private val _difficulty = MutableLiveData<DifficultyLevel>()
    val difficulty: LiveData<DifficultyLevel> = _difficulty

    private val _armScriptHayeren = MutableLiveData(appPrefs.getArmScript())
    val armScriptHayeren: LiveData<Boolean> = _armScriptHayeren

    val state: StateFlow<CombinedGroupsSettingsUi> = observeAllGroups().map { domain ->
        CombinedGroupsSettingsUi(
            featured = domain.featured.map(groupSettingsUiMapper::map),
            userGroups = domain.userGroups.map(groupSettingsUiMapper::map),
            globalGroups = domain.globalGroups.map(groupSettingsUiMapper::map)
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = CombinedGroupsSettingsUi()
    )

    init {
        viewModelScope.launch {
            appPrefs.observeStudyLanguage().collect { newLanguage ->
                _studyLanguage.value = newLanguage
            }
        }

        viewModelScope.launch {
            appPrefs.observeUiLanguage().collect { newLanguage ->
                _uiLanguage.value = newLanguage
            }
        }

        val ui = appPrefs.getUiLanguage()
        val study = appPrefs.getStudyLanguage()
        _uiLanguage.value = ui
        _studyLanguage.value = study
        rebuild(ui, study)

        viewModelScope.launch {
            observeLevelsUC.observe().collect { levels ->
                _levels.value = levels.ifEmpty { setOf(LanguageLevel.A1) }
            }
        }

        loadDifficulty()
    }

    fun onToggle(key: String) = viewModelScope.launch {
        toggleUC(key)
    }

    fun onSave(selectedKeys: Set<String>) = viewModelScope.launch {
        saveSelectionUC(selectedKeys)
    }

    fun onLanguagePicked(newLang: Language, currentLangMode: LanguageAdapterState) {
        when (currentLangMode) {
            LanguageAdapterState.UI -> {
                val study = _studyLanguage.value ?: Language.SPANISH
                _uiLanguage.value = newLang
                appPrefs.save(newLang, study)
                applyLocale(newLang)
                rebuild(newLang, study)
                _restartActivity.value = Unit
            }

            LanguageAdapterState.STUDY -> {
                val ui = _uiLanguage.value ?: Language.RUSSIAN
                _studyLanguage.value = newLang
                appPrefs.save(ui, newLang)
                rebuild(ui, newLang)
            }
        }
    }


    fun applyLocale(newUiLang: Language): Context {
        val locale = newUiLang.toLocale()
        Locale.setDefault(locale)
        val config = app.resources.configuration
        config.setLocale(locale)
        return app.createConfigurationContext(config)
    }

    fun loadDifficulty() {
        _difficulty.value = appPrefs.getDifficulty()
    }

    fun onDifficultyPicked(level: DifficultyLevel) {
        _difficulty.value = level
        appPrefs.saveDifficulty(level)
    }

    fun toggleLevel(level: LanguageLevel) {
        val current = _levels.value ?: setOf(LanguageLevel.A1)
        val new = if (current.contains(level)) current - level else current + level

        if (new.isEmpty()) {
            Toast.makeText(app, getString(app, R.string.settings_minimum_one), Toast.LENGTH_LONG)
                .show()
            return
        }

        _levels.value = new

        viewModelScope.launch {
            saveLevelsUC(new)
            appPrefs.saveLevels(new)
        }
    }

    fun onArmScriptPicked(isHayeren: Boolean) {
        _armScriptHayeren.value = isHayeren
        appPrefs.saveArmScript(isHayeren)
    }

    private fun rebuild(ui: Language, study: Language) {
        _uiLanguageList.value = Language.entries.filter { it != study }
        _studyLanguageList.value = Language.entries.filter { it != ui }
    }
}
