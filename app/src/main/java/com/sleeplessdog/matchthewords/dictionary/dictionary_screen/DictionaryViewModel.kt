package com.sleeplessdog.matchthewords.dictionary.dictionary_screen

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sleeplessdog.matchthewords.R
import com.sleeplessdog.matchthewords.backend.data.repository.AppPrefs
import com.sleeplessdog.matchthewords.backend.domain.models.CombinedGroupsDictionaryScreen
import com.sleeplessdog.matchthewords.backend.domain.models.GlobalGroupUiEntity
import com.sleeplessdog.matchthewords.backend.domain.models.UserGroupUiEntity
import com.sleeplessdog.matchthewords.backend.domain.usecases.CreateUserGroupUC
import com.sleeplessdog.matchthewords.backend.domain.usecases.GetGlobalGroupsOnceUC
import com.sleeplessdog.matchthewords.backend.domain.usecases.GetWordsCountUserGroupUC
import com.sleeplessdog.matchthewords.backend.domain.usecases.ObserveUserGroupsUC
import com.sleeplessdog.matchthewords.utils.SupportFunctions.getGroupUiName
import com.sleeplessdog.matchthewords.utils.groupIconRes
import com.sleeplessdog.matchthewords.utils.groupTitleRes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DictionaryViewModel(
    private val getGlobalGroupsOnce: GetGlobalGroupsOnceUC,
    private val getWordsCountUserGroup: GetWordsCountUserGroupUC,
    private val observeUserGroups: ObserveUserGroupsUC,
    private val createUserGroup: CreateUserGroupUC,
    private val appPrefs: AppPrefs,
    private val app: Application,
) : ViewModel() {

    private val _categoriesGrouped = MutableStateFlow(
        CombinedGroupsDictionaryScreen(userGroups = emptyList(), globalGroups = emptyList())
    )
    val categoriesGrouped: StateFlow<CombinedGroupsDictionaryScreen> = _categoriesGrouped

    init {
        observeUserGroups()
        fetchGlobalGroups()
    }

    private fun observeUserGroups() {
        viewModelScope.launch {
            observeUserGroups.invoke().collect { userGroups ->
                val userUiGroups = userGroups.map { group ->
                    val icon =
                        if (group.icon == null) R.drawable.icon_book else app.groupIconRes(group.icon)
                    UserGroupUiEntity(
                        groupKey = group.groupKey,
                        title = group.title,
                        icon = icon,
                        wordsCount = getWordsCountUserGroup(group.groupKey)
                    )
                }

                _categoriesGrouped.update { currentState ->
                    currentState.copy(userGroups = userUiGroups)
                }
            }
        }
    }

    private fun fetchGlobalGroups() {
        viewModelScope.launch {

            val globalGroups = getGlobalGroupsOnce()
            val globalUiGroups = globalGroups.map { group ->
                val titleRes = app.groupTitleRes(group.groupKey)
                if (titleRes != 0) {
                    app.getString(titleRes)
                } else {
                    group.groupKey // fallback, НИКОГДА не падает
                }
                GlobalGroupUiEntity(
                    groupId = group.groupKey,
                    title = getGroupUiName(app, 0, group.groupKey),
                    iconRes = app.groupIconRes(group.groupKey),
                    wordsCount = group.wordsCount
                )
            }
            _categoriesGrouped.update { currentState ->
                currentState.copy(globalGroups = globalUiGroups)
            }
        }
    }

    fun addNewUserGroup(name: String) {
        viewModelScope.launch {
            createUserGroup(
                groupName = name,
            )
        }
    }
}