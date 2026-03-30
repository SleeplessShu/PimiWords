package com.sleeplessdog.pimi.games.domain.models

data class CombinedGroupsDictionaryDomain(
    val userGroups: List<GroupDictionaryDomain>,
    val globalGroups: List<GroupDictionaryDomain>,
)

data class CombinedGroupsDictionaryUi(
    val userGroups: List<GroupUiDictionary> = emptyList(),
    val globalGroups: List<GroupUiDictionary> = emptyList(),
)

data class GroupUiDictionary(
    val key: String,
    val title: String,
    val iconRes: Int,
    val wordsInGroup: Int = 0,
)

data class CombinedGroupsSettingsDomain(
    val featured: List<WordGroup> = emptyList(),
    val userGroups: List<WordGroup> = emptyList(),
    val globalGroups: List<WordGroup> = emptyList(),
)

data class CombinedGroupsSettingsUi(
    val featured: List<GroupUiSettings> = emptyList(),
    val userGroups: List<GroupUiSettings> = emptyList(),
    val globalGroups: List<GroupUiSettings> = emptyList(),
)

/**
 * используется в сеттингах, чтобы получить список всех групп
 */
data class GlobalGroupUiEntity(
    val groupId: String,        //технический id
    val title: String,          //то, что показываем пользователю
    val wordsCount: Int = 0,
    val iconRes: Int,
)

data class UserGroupUiEntity(
    val groupId: String,
    val title: String,
    val wordsCount: Int = 0,
    val icon: Int,
)

data class GroupPresentationSettingsEntity(
    val key: String,
    val isSelected: Boolean,
    val isUser: Boolean,
    val orderInBlock: Int,
)

data class GroupUiSettings(
    val key: String,
    val title: String? = null,
    val titleRes: Int,
    val iconRes: Int,
    val isSelected: Boolean,
    val isUser: Boolean,
    val orderInBlock: Int,
)

data class GlobalGroupDBEntity(
    val groupKey: String,
    val wordsCount: Int,
)

data class UserGroupDomainEntity(
    val groupKey: String,
    val title: String,
    val icon: String? = null,
)


data class WordGroup(
    val key: String,
    val title: String? = null,
    val isSelected: Boolean,
    val isUser: Boolean,
    val orderInBlock: Int,
)

data class GroupDictionaryDomain(
    val key: String,
    val title: String,
    val wordsInGroup: Int = 0,
    val isUser: Boolean,
)

/*
fun WordGroupPresentation.toDomain() = WordGroup(
    key, title,isSelected, isUser, orderInBlock
)

data class GroupsUiState(
    val featured: List<GroupUiSettings> = emptyList(),
    val user: List<GroupUiSettings> = emptyList(),
    val defaults: List<GroupUiSettings> = emptyList(),
    val loading: Boolean = true,
    val error: Throwable? = null,
)
*/
