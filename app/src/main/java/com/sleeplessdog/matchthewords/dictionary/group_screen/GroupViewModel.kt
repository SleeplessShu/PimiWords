package com.sleeplessdog.matchthewords.dictionary.group_screen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sleeplessdog.matchthewords.backend.data.repository.AppPrefs
import com.sleeplessdog.matchthewords.backend.domain.usecases.AddSingleWordToSavedWordsUC
import com.sleeplessdog.matchthewords.backend.domain.usecases.AddWordToUserGroupUC
import com.sleeplessdog.matchthewords.backend.domain.usecases.DeleteWordFromUserGroupUC
import com.sleeplessdog.matchthewords.backend.domain.usecases.EditWordInUserGroupUC
import com.sleeplessdog.matchthewords.backend.domain.usecases.GetGlobalGroupWordsOnceUC
import com.sleeplessdog.matchthewords.backend.domain.usecases.MoveWordToUserGroupUC
import com.sleeplessdog.matchthewords.backend.domain.usecases.ObserveUserGroupsForGroupsUC
import com.sleeplessdog.matchthewords.backend.domain.usecases.ObserveWordsInUserGroupUC
import com.sleeplessdog.matchthewords.dictionary.dictionary_screen.DictionaryDestinations.ARG_GROUP_ID
import com.sleeplessdog.matchthewords.dictionary.dictionary_screen.DictionaryDestinations.ARG_GROUP_NAME
import com.sleeplessdog.matchthewords.dictionary.dictionary_screen.DictionaryDestinations.ARG_GROUP_TYPE
import com.sleeplessdog.matchthewords.dictionary.models.UserGroupShort
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GroupViewModel(
    private val observeUserGroupsForGroups: ObserveUserGroupsForGroupsUC,
    private val observeWordsInUserGroup: ObserveWordsInUserGroupUC,
    private val getGlobalGroupWordsOnce: GetGlobalGroupWordsOnceUC,
    private val addWordToUserGroup: AddWordToUserGroupUC,
    private val addSingleWordToSavedWords: AddSingleWordToSavedWordsUC,
    private val editWordInUserGroup: EditWordInUserGroupUC,
    private val deleteWordFromUserGroup: DeleteWordFromUserGroupUC,
    private val moveWordToUserGroup: MoveWordToUserGroupUC,
    private val appPrefs: AppPrefs,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    val ui = appPrefs.getUiLanguage()
    val study = appPrefs.getStudyLanguage()


    private val groupId: String = savedStateHandle.get<String>(ARG_GROUP_ID) ?: ""

    private val groupTitle: String = savedStateHandle.get<String>(ARG_GROUP_NAME) ?: ""

    private val groupType: GroupType =
        savedStateHandle.get<String>(ARG_GROUP_TYPE)
            ?.let { GroupType.valueOf(it) }
            ?: GroupType.GLOBAL

    private val _state = MutableStateFlow(GroupScreenState())
    val state: StateFlow<GroupScreenState> = _state

    init {
        observeUserGroups()

        when (groupType) {
            GroupType.USER -> observeOneUserGroup()
            GroupType.GLOBAL -> loadGlobalGroupOnce()
        }
    }

    private fun observeOneUserGroup() {
        viewModelScope.launch {
            observeWordsInUserGroup(groupId, ui = ui, study = study).collect { words ->

                _state.value = GroupScreenState(
                    groupType = groupType,
                    groupId = groupId,
                    groupTitle = groupTitle,
                    words = words,
                    wordsCount = words.size,
                    loading = false
                )
            }
        }
    }

    private fun observeUserGroups() {
        viewModelScope.launch {
            observeUserGroupsForGroups()
                .collect { groups ->
                    val shortGroups = groups
                        .filter { it.key != groupId }
                        .map { group ->
                            UserGroupShort(
                                groupKey = group.key,
                                title = group.title
                            )
                        }

                    _state.update { current ->
                        current.copy(
                            groups = shortGroups
                        )
                    }
                }
        }
    }


    private fun loadGlobalGroupOnce() {
        viewModelScope.launch {
            val words = getGlobalGroupWordsOnce(groupId, ui = ui, study = study)

            _state.value = GroupScreenState(
                groupType = groupType,
                groupId = groupId,
                groupTitle = groupTitle,
                words = words,
                wordsCount = words.size,
                loading = false
            )
        }
    }

    fun addNewUserWordsPair(origin: String, translate: String) {
        viewModelScope.launch {
            addWordToUserGroup(
                groupId = groupId,
                origin = origin,
                translate = translate,
                study = study,
                ui = ui
            )
        }
    }

    fun onEditWord(wordUi: WordUi) {
        viewModelScope.launch {
            editWordInUserGroup(
                groupId,
                wordUi.id,
                wordUi.word,
                wordUi.translation,
                study,
                ui
            )
        }
    }

    fun onMoveWord(wordUi: WordUi, targetGroupId: String) {
        viewModelScope.launch {
            moveWordToUserGroup(wordUi.id, targetGroupId)
        }
    }

    fun onDeleteWord(wordUi: WordUi) {
        viewModelScope.launch {
            deleteWordFromUserGroup(groupId, wordUi.id)
        }
    }

    fun onSaveToSavedWords(wordUi: WordUi) {
        viewModelScope.launch {
            addSingleWordToSavedWords(wordUi)
        }
    }
}
