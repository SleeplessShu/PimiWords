package com.sleeplessdog.matchthewords.backend.domain.usecases.settings

import com.sleeplessdog.matchthewords.backend.data.db.user.UserDao
import com.sleeplessdog.matchthewords.backend.domain.models.UserSettingsEntity
import kotlinx.coroutines.flow.firstOrNull

class SettingsSaveSelectionUC(
    private val userDao: UserDao,
) {

    suspend fun save(selectedKeys: Set<String>) {
        val current = userDao.observeSettings().firstOrNull()

        val updated = UserSettingsEntity(
            id = 1,
            languageLevels = current?.languageLevels ?: "",
            selectedGroups = selectedKeys.joinToString(",")
        )

        userDao.saveSettings(updated)
    }
}