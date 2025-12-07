package com.sleeplessdog.matchthewords.game.presentation.fragments

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sleeplessdog.matchthewords.game.data.repositories.AppPrefs
import com.sleeplessdog.matchthewords.game.domain.models.LanguageLevel
import com.sleeplessdog.matchthewords.game.domain.models.WordCategory
import com.sleeplessdog.matchthewords.game.domain.usecase.CreateUserCategoryUC
import com.sleeplessdog.matchthewords.game.domain.usecase.ObserveAllCategoriesGroupedUC
import com.sleeplessdog.matchthewords.game.domain.usecase.ObserveFeaturedCategoriesUC
import com.sleeplessdog.matchthewords.game.domain.usecase.SaveSelectionUC
import com.sleeplessdog.matchthewords.game.domain.usecase.ToggleCategoryUC
import com.sleeplessdog.matchthewords.game.presentation.models.CategoriesUiState
import com.sleeplessdog.matchthewords.game.presentation.models.CategoryUi
import com.sleeplessdog.matchthewords.game.presentation.models.DifficultLevel
import com.sleeplessdog.matchthewords.game.presentation.models.Language
import com.sleeplessdog.matchthewords.utils.ConstantsApp
import com.sleeplessdog.matchthewords.utils.ConstantsApp.FEATURED_LIMIT
import com.sleeplessdog.matchthewords.utils.SupportFunctions.drawableIdByName
import com.sleeplessdog.matchthewords.utils.SupportFunctions.stringByName
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val observeFeaturedUC: ObserveFeaturedCategoriesUC,
    private val observeAllGroupedUC: ObserveAllCategoriesGroupedUC,
    private val toggleUC: ToggleCategoryUC,
    private val saveSelectionUC: SaveSelectionUC,
    private val createUserUC: CreateUserCategoryUC,
    private val app: Application,
    private val appPrefs: AppPrefs
) : ViewModel() {
    private val _uiLanguage = MutableLiveData<Language>()
    val uiLanguage: LiveData<Language> = _uiLanguage

    private val _studyLanguage = MutableLiveData<Language>()
    val studyLanguage: LiveData<Language> = _studyLanguage

    private val _uiLanguageList = MutableLiveData<List<Language>>()
    val uiLanguageList: LiveData<List<Language>> = _uiLanguageList

    private val _studyLanguageList = MutableLiveData<List<Language>>()
    val studyLanguageList: LiveData<List<Language>> = _studyLanguageList


    private val _levels = MutableLiveData<Set<LanguageLevel>>()
    val levels: LiveData<Set<LanguageLevel>> = _levels

    private val _difficulty = MutableLiveData<DifficultLevel>()
    val difficulty: LiveData<DifficultLevel> = _difficulty


    private val _state = MutableStateFlow(CategoriesUiState())
    val state: StateFlow<CategoriesUiState> = _state

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

        loadLevels()
        loadDifficulty()

        viewModelScope.launch {
            combine(
                observeFeaturedUC(ConstantsApp.FEATURED_LIMIT), observeAllGroupedUC()
            ) { featured, grouped ->
                val toUi: (WordCategory) -> CategoryUi = { m ->
                    val uiLang = _uiLanguage.value ?: appPrefs.getUiLanguage()

                    CategoryUi(
                        key = m.key,
                        title = app.stringByName(m.titleKey, uiLang),
                        iconRes = app.drawableIdByName(m.iconKey),
                        isSelected = m.isSelected,
                        isUser = m.isUser
                    )
                }
                val userDomain = grouped.user
                val defaultDomain = grouped.defaults

                val allDomain = userDomain + defaultDomain

                val featuredDomain =
                    allDomain.sortedWith(compareByDescending<WordCategory> { it.isSelected }
                        .thenByDescending { it.isUser }
                        .thenBy { it.orderInBlock }
                        .thenBy { it.titleKey })
                        .take(FEATURED_LIMIT)

                CategoriesUiState(
                    featured = featuredDomain.map(toUi),
                    user = userDomain.map(toUi),
                    defaults = defaultDomain.map(toUi),
                    loading = false
                )
            }.catch { _state.value = _state.value.copy(loading = false, error = it) }
                .collect { _state.value = it }
        }
    }

    fun onToggle(key: String) = viewModelScope.launch {
        toggleUC(key)
    }

    fun onSave(selectedKeys: Set<String>) = viewModelScope.launch {
        saveSelectionUC(selectedKeys)
    }

    fun onCreateUserCategory(key: String, titleKey: String, iconKey: String) =
        viewModelScope.launch {
            createUserUC(key, titleKey, iconKey)
        }

    fun onLanguagePicked(newLang: Language, currentLangMode: LanguageAdapterState) {
        when (currentLangMode) {
            LanguageAdapterState.UI -> {
                val study = _studyLanguage.value ?: Language.SPANISH
                _uiLanguage.value = newLang
                appPrefs.save(newLang, study)
                rebuild(newLang, study)
            }

            LanguageAdapterState.STUDY -> {
                val ui = _uiLanguage.value ?: Language.RUSSIAN
                _studyLanguage.value = newLang
                appPrefs.save(ui, newLang)
                rebuild(ui, newLang)
            }
        }
    }

    fun loadDifficulty() {
        _difficulty.value = appPrefs.getDifficulty()
    }

    fun onDifficultyPicked(level: DifficultLevel) {
        _difficulty.value = level
        appPrefs.saveDifficulty(level)
    }

    fun loadLevels() {
        val stored = appPrefs.getLevels()
        _levels.value = stored.ifEmpty { setOf(LanguageLevel.A1) }
    }

    fun toggleLevel(level: LanguageLevel) {
        val current = _levels.value ?: setOf(LanguageLevel.A1)

        val new = if (current.contains(level)) current - level
        else current + level

        if (new.isEmpty()) {
            Toast.makeText(app, "Надо выбрать хотя бы один", Toast.LENGTH_LONG).show()
            return
        }

        _levels.value = new
        appPrefs.saveLevels(new)
    }

    private fun rebuild(ui: Language, study: Language) {
        _uiLanguageList.value = Language.entries.filter { it != study }
        _studyLanguageList.value = Language.entries.filter { it != ui }
    }
}
