package com.sleeplessdog.pimi.games.data.repository

import com.sleeplessdog.pimi.database.AppDatabaseProvider
import com.sleeplessdog.pimi.database.global.toUi
import com.sleeplessdog.pimi.database.user.UserGroupEntity
import com.sleeplessdog.pimi.database.user.toUi
import com.sleeplessdog.pimi.dictionary.group_screen.GroupType
import com.sleeplessdog.pimi.dictionary.group_screen.WordUi
import com.sleeplessdog.pimi.games.domain.models.CombinedGroupsDictionaryDomain
import com.sleeplessdog.pimi.games.domain.models.GlobalGroupDBEntity
import com.sleeplessdog.pimi.games.domain.models.GroupDictionaryDomain
import com.sleeplessdog.pimi.games.domain.models.GroupPresentationSettingsEntity
import com.sleeplessdog.pimi.settings.Language
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

class GroupsRepository(
    private val databaseProvider: AppDatabaseProvider,
) {


    fun observeAllGroupsForDictionary(): Flow<CombinedGroupsDictionaryDomain> {
        val globalDao = databaseProvider.getGlobalDao()
        val userDao = databaseProvider.getUserDao()
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
        val userDao = databaseProvider.getUserDao()
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


    fun observeUserGroups(): Flow<List<UserGroupEntity>> {
        val userDao = databaseProvider.getUserDao()
        return userDao.observeUserGroups()
    }

    suspend fun getGlobalGroupsOnce(): List<GlobalGroupDBEntity> {

        val globalDao = databaseProvider.getGlobalDatabase().globalDao()
        val globalKeys = globalDao.getAllGroupKeys()

        val globalCategories = globalKeys.map { key ->
            GlobalGroupDBEntity(
                groupKey = key, wordsCount = getWordsCountGlobalGroup(key)
            )
        }
        return globalCategories
    }


    suspend fun createUserGroup(
        key: String,
        groupName: String,
    ) {
        val userDao = databaseProvider.getUserDatabase().userDao()
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
        val userDao = databaseProvider.getUserDatabase().userDao()
        userDao.updateGroupTitle(
            groupKey = groupKey, newTitle = newName
        )
    }

    suspend fun deleteUserGroup(
        groupKey: String,
    ) {
        val userDao = databaseProvider.getUserDatabase().userDao()
        userDao.deleteGroupByKey(
            groupKey = groupKey,
        )
    }

    suspend fun getWordsCount(group: GroupPresentationSettingsEntity): Int {
        val userDao = databaseProvider.getUserDatabase().userDao()
        val globalDao = databaseProvider.getGlobalDatabase().globalDao()

        return if (group.isUser) {
            userDao.countWordsByGroupKey(group.key)
        } else {
            globalDao.countWordsByGroup(group.key)
        }
    }

    suspend fun getWordsCountUserGroup(groupKey: String): Int {
        val userDao = databaseProvider.getUserDatabase().userDao()
        return userDao.countWordsByGroupKey(groupKey)
    }

    private suspend fun getWordsCountGlobalGroup(groupKey: String): Int {
        val globalDao = databaseProvider.getGlobalDatabase().globalDao()
        return globalDao.countWordsByGroup(groupKey)
    }

    fun observeWordsInUserGroup(
        groupId: String, ui: Language, study: Language,
    ): Flow<List<WordUi>> {
        val userDao = databaseProvider.getUserDao()
        return userDao.observeWordsInUserGroup(groupId)
            .map { list -> list.map { it.toUi(ui, study) } }
    }

    suspend fun getGlobalGroupWordsOnceUC(
        groupId: String, ui: Language, study: Language,
    ): List<WordUi> {
        val globalDao = databaseProvider.getGlobalDatabase().globalDao()
        return globalDao.getWordsByGroup(groupId).map { it.toUi(ui, study) }
    }

    suspend fun getGroupTitleById(
        groupId: String, groupType: GroupType,
    ): String {
        val userDao = databaseProvider.getUserDatabase().userDao()
        val globalDao = databaseProvider.getGlobalDatabase().globalDao()

        return when (groupType) {
            GroupType.USER -> userDao.getGroupTitleById(groupId) ?: groupId
            GroupType.GLOBAL -> globalDao.getGroupTitleById(groupId) ?: groupId
        }
    }

    suspend fun getSelectedGroupsUC(): Set<String> {
        val userDao = databaseProvider.getUserDatabase().userDao()
        val raw = userDao.getSelectedGroups()

        return raw
            ?.split(",")
            ?.map { it.trim() }
            ?.filter { it.isNotEmpty() }
            ?.toSet()
            ?: emptySet()
    }
}