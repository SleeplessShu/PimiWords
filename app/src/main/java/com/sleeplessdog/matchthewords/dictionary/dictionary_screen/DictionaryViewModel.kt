package com.sleeplessdog.matchthewords.dictionary.dictionary_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sleeplessdog.matchthewords.backend.domain.models.CombinedGroupsDictionaryUi
import com.sleeplessdog.matchthewords.backend.domain.usecases.CreateUserGroupUC
import com.sleeplessdog.matchthewords.backend.domain.usecases.DeleteUserGroupUC
import com.sleeplessdog.matchthewords.backend.domain.usecases.ObserveAllGroupsForDictionaryUC
import com.sleeplessdog.matchthewords.backend.domain.usecases.RenameUserGroupUC
import com.sleeplessdog.matchthewords.dictionary.GroupDictionaryUiMapper
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DictionaryViewModel(
    observeAllGroups: ObserveAllGroupsForDictionaryUC,
    private val createUserGroup: CreateUserGroupUC,
    private val renameUserGroup: RenameUserGroupUC,
    private val deleteUserGroup: DeleteUserGroupUC,
    private val groupDictionaryUiMapper: GroupDictionaryUiMapper,

    ) : ViewModel() {

    val state: StateFlow<CombinedGroupsDictionaryUi> =
        observeAllGroups()
            .map { domain ->
                CombinedGroupsDictionaryUi(
                    userGroups = domain.userGroups.map(groupDictionaryUiMapper::map),
                    globalGroups = domain.globalGroups.map(groupDictionaryUiMapper::map)
                )
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = CombinedGroupsDictionaryUi()
            )

    fun addNewUserGroup(name: String) {
        viewModelScope.launch {
            createUserGroup(
                groupName = name,
            )
        }
    }

    fun renameGroup(groupKey: String, newName: String) {
        viewModelScope.launch {
            renameUserGroup(groupKey, newName)
        }
    }

    fun deleteGroup(groupKey: String) {
        viewModelScope.launch {
            deleteUserGroup(groupKey)
        }
    }
}
