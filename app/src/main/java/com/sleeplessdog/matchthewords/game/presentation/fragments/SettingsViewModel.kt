package com.sleeplessdog.matchthewords.game.presentation.fragments

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sleeplessdog.matchthewords.backend.data.repository.AppPrefs
import com.sleeplessdog.matchthewords.backend.domain.models.CombinedGroupsSettingsUi
import com.sleeplessdog.matchthewords.backend.domain.models.LanguageLevel
import com.sleeplessdog.matchthewords.backend.domain.usecases.CreateUserGroupUC
import com.sleeplessdog.matchthewords.backend.domain.usecases.ObserveAllGroupsGroupedUC
import com.sleeplessdog.matchthewords.backend.domain.usecases.SaveSelectionUC
import com.sleeplessdog.matchthewords.backend.domain.usecases.ToggleCategoryUC
import com.sleeplessdog.matchthewords.backend.domain.usecases.settings.SettingsObserveLevelsUC
import com.sleeplessdog.matchthewords.backend.domain.usecases.settings.SettingsSaveLevelsUC
import com.sleeplessdog.matchthewords.dictionary.models.GroupUiMapper
import com.sleeplessdog.matchthewords.game.presentation.holders.LanguageAdapterState
import com.sleeplessdog.matchthewords.game.presentation.models.DifficultLevel
import com.sleeplessdog.matchthewords.game.presentation.models.Language
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    //private val observeFeaturedUC: ObserveFeaturedGroupsUC,
    observeAllGroups: ObserveAllGroupsGroupedUC,
    private val toggleUC: ToggleCategoryUC,
    private val saveSelectionUC: SaveSelectionUC,
    private val createUserGroupUC: CreateUserGroupUC,
    private val saveLevelsUC: SettingsSaveLevelsUC,
    private val observeLevelsUC: SettingsObserveLevelsUC,
    private val groupUiMapper: GroupUiMapper,
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


    private val _levels = MutableLiveData<Set<LanguageLevel>>()
    val levels: LiveData<Set<LanguageLevel>> = _levels

    private val _difficulty = MutableLiveData<DifficultLevel>()
    val difficulty: LiveData<DifficultLevel> = _difficulty


    val state: StateFlow<CombinedGroupsSettingsUi> =
        observeAllGroups()
            .map { domain ->
                CombinedGroupsSettingsUi(
                    featured = domain.featured.map(groupUiMapper::map),
                    userGroups = domain.userGroups.map(groupUiMapper::map),
                    globalGroups = domain.globalGroups.map(groupUiMapper::map)
                )
            }
            .stateIn(
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
            observeLevelsUC.observe()
                .collect { levels ->
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

    fun toggleLevel(level: LanguageLevel) {
        val current = _levels.value ?: setOf(LanguageLevel.A1)

        val new = if (current.contains(level)) current - level
        else current + level

        if (new.isEmpty()) {
            Toast.makeText(app, "Надо выбрать хотя бы один", Toast.LENGTH_LONG).show()
            return
        }

        _levels.value = new

        viewModelScope.launch {
            saveLevelsUC.save(new)
        }

    }

    private fun rebuild(ui: Language, study: Language) {
        _uiLanguageList.value = Language.entries.filter { it != study }
        _studyLanguageList.value = Language.entries.filter { it != ui }
    }

    /*private fun WordGroup.toUi(): GroupUiSettings {
        return GroupUiSettings(
            key = key,
            titleRes = if (isUser && !key.equals("saved_words")) 0 else app.groupTitleRes(
                key
            ),
            title = if (isUser && !key.equals("saved_words")) title else null,
            iconRes = if (isUser && !key.equals("saved_words")) R.drawable.ic_group_default else app.groupIconRes(
                key
            ),
            isSelected = isSelected,
            isUser = isUser,
            orderInBlock = orderInBlock
        )
    }*/

    /*private fun buildFeatured(
        featuredGroups: List<WordGroup>,
        userGroups: List<WordGroup>,
        globalGroups: List<WordGroup>,
    ): List<WordGroup> {

        // уже хватает — просто обрезаем
        if (featuredGroups.size >= FEATURED_LIMIT) {
            return featuredGroups.take(FEATURED_LIMIT)
        }

        val result = featuredGroups.toMutableList()
        val usedKeys = result.map { it.key }.toMutableSet()

        // добираем из user
        for (group in userGroups) {
            if (result.size >= FEATURED_LIMIT) break
            if (group.key !in usedKeys) {
                result.add(group)
                usedKeys.add(group.key)
            }
        }

        // добираем из global
        for (group in globalGroups) {
            if (result.size >= FEATURED_LIMIT) break
            if (group.key !in usedKeys) {
                result.add(group)
                usedKeys.add(group.key)
            }
        }

        return result
    }*/
}
