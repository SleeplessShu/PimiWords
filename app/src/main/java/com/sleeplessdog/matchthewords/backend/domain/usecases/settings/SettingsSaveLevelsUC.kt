package com.sleeplessdog.matchthewords.backend.domain.usecases.settings

import com.sleeplessdog.matchthewords.backend.data.db.user.UserDao
import com.sleeplessdog.matchthewords.backend.domain.models.UserSettingsEntity
import com.sleeplessdog.matchthewords.backend.domain.models.LanguageLevel
import kotlinx.coroutines.flow.firstOrNull

class SettingsSaveLevelsUC(
    private val userDao: UserDao,
) {
    suspend fun save(levels: Set<LanguageLevel>) {
        val current = userDao.observeSettings().firstOrNull()

        userDao.saveSettings(
            UserSettingsEntity(
                id = 1,
                languageLevels = levels.joinToString(",") { it.name },
                selectedGroups = current?.selectedGroups ?: ""
            )
        )
    }
}