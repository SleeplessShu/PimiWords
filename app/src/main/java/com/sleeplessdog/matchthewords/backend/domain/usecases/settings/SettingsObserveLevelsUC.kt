package com.sleeplessdog.matchthewords.backend.domain.usecases.settings

import com.sleeplessdog.matchthewords.backend.data.db.user.UserDao
import com.sleeplessdog.matchthewords.backend.domain.models.LanguageLevel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SettingsObserveLevelsUC(
    private val userDao: UserDao,
) {
    fun observe(): Flow<Set<LanguageLevel>> = userDao.observeSettings().map { entity ->
        entity?.languageLevels?.split(",")
            ?.mapNotNull { runCatching { LanguageLevel.valueOf(it) }.getOrNull() }?.toSet()
            ?: setOf(LanguageLevel.A1)
    }
}
