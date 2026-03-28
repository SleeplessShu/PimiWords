package com.sleeplessdog.pimi.settings

import com.sleeplessdog.pimi.games.data.repository.SettingsRepository
import com.sleeplessdog.pimi.games.domain.models.CombinedGroupsSettingsDomain
import kotlinx.coroutines.flow.Flow

class SettingsObserveLevelsUC(
    private val repo: SettingsRepository,
) {
    fun observe(): Flow<Set<LanguageLevel>> = repo.observeLevels()
}

class SettingsSaveLevelsUC(
    private val repo: SettingsRepository,
) {
    suspend operator fun invoke(levels: Set<LanguageLevel>) {
        repo.settingsSaveLevels(levels)
    }
}

class SettingsSaveSelectionUC(
    private val repo: SettingsRepository,
) {
    suspend operator fun invoke(keys: Set<String>) {
        repo.saveSelection(keys)
    }
}

class SettingsToggleCategoryUC(
    private val repo: SettingsRepository,
) {
    suspend operator fun invoke(key: String) {
        repo.toggle(key)
    }
}

class ObserveAllGroupsForSettingsUC(
    private val repo: SettingsRepository,
) {
    operator fun invoke(): Flow<CombinedGroupsSettingsDomain> = repo.observeAllGroupsForSettings()
}