package com.sleeplessdog.pimi.games.data.repository

import com.sleeplessdog.pimi.database.AppDatabaseProvider
import com.sleeplessdog.pimi.database.global.toUi
import com.sleeplessdog.pimi.database.user.UserGroupEntity
import com.sleeplessdog.pimi.database.user.toUi
import com.sleeplessdog.pimi.dictionary.authorisation.DatabaseInstance
import com.sleeplessdog.pimi.dictionary.group_screen.GroupType
import com.sleeplessdog.pimi.dictionary.group_screen.WordUi
import com.sleeplessdog.pimi.games.domain.models.CombinedGroupsDictionaryDomain
import com.sleeplessdog.pimi.games.domain.models.GlobalGroupDBEntity
import com.sleeplessdog.pimi.games.domain.models.GroupDictionaryDomain
import com.sleeplessdog.pimi.games.domain.models.GroupPresentationSettingsEntity
import com.sleeplessdog.pimi.settings.Language
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

class GroupsRepository(
    private val databaseProvider: AppDatabaseProvider,
    private val deployCompleted: SharedFlow<DatabaseInstance>,
    private val appPrefs: AppPrefs,
) {


    @OptIn(ExperimentalCoroutinesApi::class)
    fun observeAllGroupsForDictionary(): Flow<CombinedGroupsDictionaryDomain> {
        return deployCompleted
            .onStart { emit(DatabaseInstance.USER) }
            .flatMapLatest {
                val globalDao = databaseProvider.getGlobalDao()
                val userDao = databaseProvider.getUserDao()
                combine(
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
    }

    fun observeUserGroupsForDictionary(): Flow<List<GroupDictionaryDomain>> {
        return deployCompleted
            .onStart { emit(DatabaseInstance.USER) }
            .flatMapLatest {
                val userDao = databaseProvider.getUserDao()
                userDao.observeUserGroups().map { list ->
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
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun observeUserGroups(): Flow<List<UserGroupEntity>> {
        return deployCompleted
            .onStart { emit(DatabaseInstance.USER) }
            .flatMapLatest {
                val userDao = databaseProvider.getUserDao()
                userDao.observeUserGroups()
            }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun observeWordsInUserGroup(
        groupId: String, ui: Language, study: Language,
    ): Flow<List<WordUi>> {
        return deployCompleted
            .onStart { emit(DatabaseInstance.USER) }
            .flatMapLatest {
                val userDao = databaseProvider.getUserDao()
                userDao.observeWordsInUserGroup(groupId)
                    .map { list -> list.map { it.toUi(ui, study) } }
            }
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
        appPrefs.markLocalDatabaseDirty()
    }

    suspend fun renameUserGroup(
        groupKey: String,
        newName: String,
    ) {
        val userDao = databaseProvider.getUserDatabase().userDao()
        userDao.updateGroupTitle(
            groupKey = groupKey, newTitle = newName
        )
        appPrefs.markLocalDatabaseDirty()
    }

    suspend fun deleteUserGroup(
        groupKey: String,
    ) {
        val userDao = databaseProvider.getUserDatabase().userDao()
        userDao.deleteGroupByKey(
            groupKey = groupKey,
        )
        appPrefs.markLocalDatabaseDirty()
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