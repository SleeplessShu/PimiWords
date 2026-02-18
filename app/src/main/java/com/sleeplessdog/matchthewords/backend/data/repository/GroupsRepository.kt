package com.sleeplessdog.matchthewords.backend.data.repository

import com.sleeplessdog.matchthewords.backend.data.db.global.GlobalDao
import com.sleeplessdog.matchthewords.backend.data.db.global.toUi
import com.sleeplessdog.matchthewords.backend.data.db.user.UserDao
import com.sleeplessdog.matchthewords.backend.data.db.user.UserGroupEntity
import com.sleeplessdog.matchthewords.backend.data.db.user.toUi
import com.sleeplessdog.matchthewords.backend.domain.models.CombinedGroupsDictionaryDomain
import com.sleeplessdog.matchthewords.backend.domain.models.CombinedGroupsSettingsDomain
import com.sleeplessdog.matchthewords.backend.domain.models.GlobalGroupDBEntity
import com.sleeplessdog.matchthewords.backend.domain.models.GroupDictionaryDomain
import com.sleeplessdog.matchthewords.backend.domain.models.GroupPresentationSettingsEntity
import com.sleeplessdog.matchthewords.backend.domain.models.UserSettingsEntity
import com.sleeplessdog.matchthewords.backend.domain.models.WordGroup
import com.sleeplessdog.matchthewords.dictionary.group_screen.GroupType
import com.sleeplessdog.matchthewords.dictionary.group_screen.WordUi
import com.sleeplessdog.matchthewords.game.presentation.models.Language
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

class GroupsRepository(
    private val globalDao: GlobalDao,
    private val userDao: UserDao,
) {
    fun observeAllGroupsForSettings(): Flow<CombinedGroupsSettingsDomain> {

        return combine(
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
                featured = featured, userGroups = userCategories, globalGroups = globalCategories
            )
        }
    }

    fun observeAllGroupsForDictionary(): Flow<CombinedGroupsDictionaryDomain> {

        return combine(
            userDao.observeUserGroups(), globalDao.observeAllGroupKeys()
        ) { userGroups, globalKeys ->


            val globalCategories = globalKeys.map { key ->
                GroupDictionaryDomain(
                    key = key,
                    title = key,
                    wordsInGroup = getWordsCountGlobalGroup(key),
                    isUser = false
                )
            }

            val userCategories = userGroups.map { g ->
                GroupDictionaryDomain(
                    key = g.groupKey,
                    title = g.title,
                    wordsInGroup = getWordsCountUserGroup(g.groupKey),
                    isUser = true
                )
            }

            CombinedGroupsDictionaryDomain(
                userGroups = userCategories, globalGroups = globalCategories
            )
        }
    }

    fun observeUserGroupsForDictionary(): Flow<List<GroupDictionaryDomain>> {
        return userDao.observeUserGroups().map { list ->
            list.map { group ->
                GroupDictionaryDomain(
                    key = group.groupKey,
                    title = group.title,
                    wordsInGroup = getWordsCountUserGroup(group.groupKey),
                    isUser = true
                )
            }
        }
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

    fun observeUserGroups(): Flow<List<UserGroupEntity>> {
        return userDao.observeUserGroups()
    }

    suspend fun getGlobalGroupsOnce(): List<GlobalGroupDBEntity> {
        val globalKeys = globalDao.getAllGroupKeys()

        val globalCategories = globalKeys.map { key ->
            GlobalGroupDBEntity(
                groupKey = key, wordsCount = getWordsCountGlobalGroup(key)
            )
        }
        return globalCategories
    }

    suspend fun toggle(key: String) {
        val selected =
            userDao.getSelectedGroups()?.split(",")?.filter { it.isNotBlank() }?.toMutableSet()
                ?: mutableSetOf()

        if (key in selected) selected.remove(key)
        else selected.add(key)

        saveSelection(selected)
    }

    suspend fun createUserGroup(
        key: String,
        groupName: String,
    ) {
        userDao.insertGroup(
            UserGroupEntity(
                groupKey = key, title = groupName
            )
        )
    }

    suspend fun renameUserGroup(
        groupKey: String,
        newName: String,
    ) {
        userDao.updateGroupTitle(
            groupKey = groupKey, newTitle = newName
        )
    }

    suspend fun deleteUserGroup(
        groupKey: String,
    ) {
        userDao.deleteGroupByKey(
            groupKey = groupKey,
        )
    }

    suspend fun getWordsCount(group: GroupPresentationSettingsEntity): Int {
        return if (group.isUser) {
            userDao.countWordsByGroupKey(group.key)
        } else {
            globalDao.countWordsByGroup(group.key)
        }
    }

    suspend fun getWordsCountUserGroup(groupKey: String): Int {
        return userDao.countWordsByGroupKey(groupKey)
    }

    private suspend fun getWordsCountGlobalGroup(groupKey: String): Int {
        return globalDao.countWordsByGroup(groupKey)
    }

    fun observeWordsInUserGroup(
        groupId: String, ui: Language, study: Language,
    ): Flow<List<WordUi>> {
        return userDao.observeWordsInUserGroup(groupId)
            .map { list -> list.map { it.toUi(ui, study) } }
    }

    suspend fun getGlobalGroupWordsOnceUC(
        groupId: String, ui: Language, study: Language,
    ): List<WordUi> {
        return globalDao.getWordsByGroup(groupId).map { it.toUi(ui, study) }
    }

    suspend fun getGroupTitleById(
        groupId: String, groupType: GroupType,
    ): String {
        return when (groupType) {
            GroupType.USER -> userDao.getGroupTitleById(groupId) ?: groupId
            GroupType.GLOBAL -> globalDao.getGroupTitleById(groupId) ?: groupId
        }
    }
}