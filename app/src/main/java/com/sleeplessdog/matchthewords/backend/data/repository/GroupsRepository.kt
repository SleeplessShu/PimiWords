package com.sleeplessdog.matchthewords.backend.data.repository

import com.sleeplessdog.matchthewords.backend.data.db.global.GlobalDao
import com.sleeplessdog.matchthewords.backend.data.db.user.UserDao
import com.sleeplessdog.matchthewords.backend.data.db.user.UserGroupEntity
import com.sleeplessdog.matchthewords.backend.domain.models.UserSettingsEntity
import com.sleeplessdog.matchthewords.backend.domain.models.WordGroup
import kotlinx.coroutines.flow.firstOrNull

class GroupsRepository(
    private val globalDao: GlobalDao,
    private val userDao: UserDao,
) {
    suspend fun getAllCategories(): List<WordGroup> {

        val selected = userDao.getSelectedGroups()
            ?.split(",")
            ?.filter { it.isNotBlank() }
            ?.toSet()
            ?: emptySet()

        val globalKeys = globalDao.getAllGroupKeys()

        val globalCategories = globalKeys.map { key ->
            WordGroup(
                key = key,
                isSelected = key in selected,
                isUser = false,
                orderInBlock = 0
            )
        }

        val userGroups = userDao.getAllGroupsOnce()

        val userCategories = userGroups.map { g ->
            WordGroup(
                key = g.groupKey,
                isSelected = g.groupKey in selected,
                isUser = true,
                orderInBlock = 1
            )
        }

        return userCategories + globalCategories
    }

    /**
     * сохраняти в БД выбранные в настройках группы
     */
    suspend fun saveSelection(keys: Set<String>) {
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
        val selected = userDao.getSelectedGroups()
            ?.split(",")
            ?.filter { it.isNotBlank() }
            ?.toMutableSet()
            ?: mutableSetOf()

        if (key in selected) selected.remove(key)
        else selected.add(key)

        saveSelection(selected)
    }

    suspend fun createUserCategory(
        key: String,
        title: String,
        iconKey: String,
    ) {
        userDao.insertGroup(
            UserGroupEntity(
                groupKey = key,
                title = title,
                iconKey = iconKey
            )
        )
    }

    suspend fun getWordsCount(group: WordGroup): Int {
        return if (group.isUser) {
            userDao.countWordsByGroupKey(group.key)
        } else {
            globalDao.countWordsByGroup(group.key)
        }
    }
}