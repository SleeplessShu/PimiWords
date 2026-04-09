package com.sleeplessdog.pimi.games.data.repository

import com.sleeplessdog.pimi.database.AppDatabaseProvider
import com.sleeplessdog.pimi.dictionary.authorisation.DatabaseInstance
import com.sleeplessdog.pimi.games.domain.models.CombinedGroupsSettingsDomain
import com.sleeplessdog.pimi.games.domain.models.WordGroup
import com.sleeplessdog.pimi.settings.LanguageLevel
import com.sleeplessdog.pimi.settings.UserSettingsEntity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

class SettingsRepository(
    private val databaseProvider: AppDatabaseProvider,
    private val deployCompleted: SharedFlow<DatabaseInstance>,
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    fun observeAllGroupsForSettings(): Flow<CombinedGroupsSettingsDomain> {
        return deployCompleted.onStart { emit(DatabaseInstance.USER) }.flatMapLatest {
            val globalDao = databaseProvider.getGlobalDao()
            val userDao = databaseProvider.getUserDao()

            combine(
                userDao.observeUserGroups(),
                globalDao.observeAllGroupKeys(),
                userDao.observeSelectedGroups()
            ) { userGroups, globalKeys, selectedRaw ->

                val selected =
                    selectedRaw?.split(",")?.filter { it.isNotBlank() }?.toSet() ?: emptySet()

                val globalCategories = globalKeys.map { key ->
                    WordGroup(
                        key = key,
                        isSelected = key in selected,
                        isUser = false,
                        orderInBlock = 1,
                    )
                }

                val userCategories = userGroups.map { g ->
                    WordGroup(
                        key = g.groupKey,
                        title = g.title,
                        isSelected = g.groupKey in selected,
                        isUser = true,
                        orderInBlock = 0
                    )
                }

                val featured =
                    (userCategories + globalCategories).sortedWith(compareByDescending<WordGroup> { it.isSelected }.thenByDescending { it.isUser }
                        .thenBy { it.orderInBlock }.thenBy { it.key })

                CombinedGroupsSettingsDomain(
                    featured = featured,
                    userGroups = userCategories,
                    globalGroups = globalCategories
                )
            }
        }
    }

    suspend fun saveSelection(keys: Set<String>) {
        val userDao = databaseProvider.getUserDatabase().userDao()
        val current = userDao.observeSettings().firstOrNull()

        userDao.saveSettings(
            UserSettingsEntity(
                id = 1,
                languageLevels = current?.languageLevels ?: "",
                selectedGroups = keys.joinToString(",")
            )
        )
    }

    suspend fun toggle(key: String) {
        val userDao = databaseProvider.getUserDatabase().userDao()
        val selected =
            userDao.getSelectedGroups()?.split(",")?.filter { it.isNotBlank() }?.toMutableSet()
                ?: mutableSetOf()

        if (key in selected) selected.remove(key)
        else selected.add(key)

        saveSelection(selected)
    }

    suspend fun settingsSaveLevels(levels: Set<LanguageLevel>) {
        val userDao = databaseProvider.getUserDatabase().userDao()
        val current = userDao.observeSettings().firstOrNull()

        userDao.saveSettings(
            UserSettingsEntity(
                id = 1,
                languageLevels = levels.joinToString(",") { it.name },
                selectedGroups = current?.selectedGroups ?: ""

            )
        )

    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun observeLevels(): Flow<Set<LanguageLevel>> {
        return deployCompleted.onStart { emit(DatabaseInstance.USER) }.flatMapLatest {
            val userDao = databaseProvider.getUserDao()
            userDao.observeSettings().map { entity ->
                entity?.languageLevels?.split(",")
                    ?.mapNotNull { runCatching { LanguageLevel.valueOf(it) }.getOrNull() }
                    ?.toSet() ?: setOf(LanguageLevel.A1)
            }
        }
    }
}