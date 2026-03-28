package com.sleeplessdog.pimi.games.domain.usecases

import com.sleeplessdog.pimi.database.user.UserDao
import com.sleeplessdog.pimi.database.user.UserGroupEntity
import com.sleeplessdog.pimi.dictionary.group_screen.WordUi
import com.sleeplessdog.pimi.games.data.repository.GroupsRepository
import com.sleeplessdog.pimi.games.domain.models.CombinedGroupsDictionaryDomain
import com.sleeplessdog.pimi.games.domain.models.GlobalGroupDBEntity
import com.sleeplessdog.pimi.games.domain.models.GroupDictionaryDomain
import com.sleeplessdog.pimi.games.domain.models.GroupPresentationSettingsEntity
import com.sleeplessdog.pimi.settings.Language
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class CreateUserGroupUC(
    private val repo: GroupsRepository,
) {
    suspend operator fun invoke(
        groupName: String,
    ) {
        val key = UUID.randomUUID().toString()
        repo.createUserGroup(key, groupName)
    }
}

class RenameUserGroupUC(
    private val repo: GroupsRepository,
) {
    suspend operator fun invoke(
        groupKey: String,
        newName: String,
    ) {
        repo.renameUserGroup(groupKey, newName)
    }
}

class DeleteUserGroupUC(
    private val repo: GroupsRepository,
) {
    suspend operator fun invoke(
        groupKey: String,
    ) {
        repo.deleteUserGroup(groupKey)
    }
}

class GetWordsCountForGroupUC(
    private val repository: GroupsRepository,
) {
    suspend operator fun invoke(group: GroupPresentationSettingsEntity): Int {
        return repository.getWordsCount(group)
    }
}

class GetSelectedGroupsUC(
    private val userDao: UserDao,
) {
    suspend fun get(): Set<String> {
        val raw = userDao.getSelectedGroups()

        return raw
            ?.split(",")
            ?.map { it.trim() }
            ?.filter { it.isNotEmpty() }
            ?.toSet()
            ?: emptySet()
    }
}


class ObserveWordsInUserGroupUC(
    private val repo: GroupsRepository,
) {
    operator fun invoke(
        groupId: String,
        ui: Language,
        study: Language,
    ): Flow<List<WordUi>> {
        return (repo.observeWordsInUserGroup(groupId, ui, study))
    }
}

class ObserveUserGroupsUC(
    private val repo: GroupsRepository,
) {
    operator fun invoke(): Flow<List<UserGroupEntity>> {
        return repo.observeUserGroups()
    }
}

class GetGlobalGroupsOnceUC(
    private val repo: GroupsRepository,
) {
    suspend operator fun invoke(): List<GlobalGroupDBEntity> {
        return repo.getGlobalGroupsOnce()
    }
}

class GetWordsCountUserGroupUC(
    private val repo: GroupsRepository,
) {
    suspend operator fun invoke(groupId: String): Int {
        return repo.getWordsCountUserGroup(groupId)
    }
}

class GetGlobalGroupWordsOnceUC(
    private val repo: GroupsRepository,
) {
    suspend operator fun invoke(
        groupId: String,
        ui: Language,
        study: Language,
    ): List<WordUi> {
        return repo.getGlobalGroupWordsOnceUC(groupId, ui, study)
    }
}


class ObserveAllGroupsForDictionaryUC(
    private val repo: GroupsRepository,
) {
    operator fun invoke(): Flow<CombinedGroupsDictionaryDomain> =
        repo.observeAllGroupsForDictionary()
}

class ObserveUserGroupsForGroupsUC(
    private val repo: GroupsRepository,
) {
    operator fun invoke(): Flow<List<GroupDictionaryDomain>> =
        repo.observeUserGroupsForDictionary()
}
