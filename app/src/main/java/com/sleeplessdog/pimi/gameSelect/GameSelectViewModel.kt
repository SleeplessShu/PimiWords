package com.sleeplessdog.pimi.gameSelect

import android.app.Application
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sleeplessdog.pimi.dictionary.models.GroupSettingsUiMapper
import com.sleeplessdog.pimi.games.data.repository.AppPrefs
import com.sleeplessdog.pimi.games.presentation.controller.LandingPagesController
import com.sleeplessdog.pimi.games.presentation.models.GameType
import com.sleeplessdog.pimi.settings.Language
import com.sleeplessdog.pimi.settings.ObserveAllGroupsForSettingsUC
import com.sleeplessdog.pimi.utils.ConstantsPaths.KEY_UI_LANG
import com.sleeplessdog.pimi.utils.ConstantsPaths.PREFS_NAME
import com.sleeplessdog.pimi.utils.LandingRepeatController.ALWAYS_SHOW_FIRST_LANDING
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class GameSelectViewModel(
    private val appPrefs: AppPrefs,
    private val landingManager: LandingPagesController,
    private val observeAllGroupsForSettings: ObserveAllGroupsForSettingsUC,
    private val groupSettingsUiMapper: GroupSettingsUiMapper,
    private val app: Application,
) : ViewModel() {

    private var forcedGroupKey: String? = null
    private var forcedGroupIsUser: Boolean = false

    data class NavigateToGame(
        val gameType: GameType,
        val groupKey: String?,
        val groupIsUser: Boolean = false,
    )

    private val _forcedGroupTitle = MutableLiveData<String?>(null)
    val forcedGroupTitle: LiveData<String?> = _forcedGroupTitle

    val selectedGroupTitles: StateFlow<List<String>> =
        observeAllGroupsForSettings()
            .map { domain ->
                (domain.userGroups + domain.globalGroups)
                    .filter { it.isSelected }
                    .map { group ->
                        val mapped = groupSettingsUiMapper.map(group)
                        when {
                            mapped.title != null -> mapped.title
                            mapped.titleRes != 0 -> localizedContext().getString(mapped.titleRes)
                            else -> group.key
                        }
                    }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
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
        val isFirstRun = landingManager.shouldShow(LandingKeys.APP_FIRST_LAUNCH)
        _showLanding.value = isFirstRun

        viewModelScope.launch {
            appPrefs.observeStudyLanguage().collect { newLanguage ->
                _studyLanguage.value = newLanguage
            }
        }

        val ui = appPrefs.getUiLanguage()
        rebuild(ui)
    }

    fun setForcedGroup(key: String?, isUser: Boolean = false) {
        forcedGroupKey = key
        forcedGroupIsUser = isUser

        if (key == null) {
            _forcedGroupTitle.value = null
            return
        }

        viewModelScope.launch {
            val domain = observeAllGroupsForSettings().first()
            val allGroups = domain.userGroups + domain.globalGroups
            val found = allGroups.find { it.key == key }

            _forcedGroupTitle.value = if (found != null) {
                val mapped = groupSettingsUiMapper.map(found)
                when {
                    mapped.title != null -> mapped.title
                    mapped.titleRes != 0 -> localizedContext().getString(mapped.titleRes)
                    else -> key
                }
            } else {
                key
            }
        }
    }

    fun clearForcedGroup() {
        forcedGroupKey = null
        forcedGroupIsUser = false
        _forcedGroupTitle.value = null
    }

    fun onLandingShown() {
        _showLanding.value = false
        if (!ALWAYS_SHOW_FIRST_LANDING) {
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

    private fun localizedContext(): Context {
        val prefs = app.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val langName = prefs.getString(KEY_UI_LANG, null)
        val language = langName?.let {
            runCatching { Language.valueOf(it) }.getOrNull()
        } ?: Language.ENGLISH

        val locale = language.toLocale()
        val config = app.resources.configuration
        config.setLocale(locale)
        return app.createConfigurationContext(config)
    }
}
