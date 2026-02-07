package com.sleeplessdog.matchthewords.dictionary

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sleeplessdog.matchthewords.backend.data.repository.AppPrefs
import com.sleeplessdog.matchthewords.backend.domain.models.WordGroup
import com.sleeplessdog.matchthewords.backend.domain.usecases.groups.GetWordsCountForGroupUC
import com.sleeplessdog.matchthewords.backend.domain.usecases.groups.ObserveAllGroupsGroupedUC
import com.sleeplessdog.matchthewords.utils.groupIconRes
import com.sleeplessdog.matchthewords.utils.groupTitleRes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DictionaryViewModel(
    private val observeAllGroups: ObserveAllGroupsGroupedUC,
    private val getWordsCountForGroup: GetWordsCountForGroupUC,
    private val appPrefs: AppPrefs,
    private val app: Application,
) : ViewModel() {

    private val _categoriesGrouped = MutableStateFlow(
        DictionaryScreenState(userGroups = emptyList(), defaultGroups = emptyList())
    )
    val categoriesGrouped: StateFlow<DictionaryScreenState> = _categoriesGrouped

    init {
        loadGroups()
    }

    private fun loadGroups() {
        viewModelScope.launch {

            observeAllGroups().collect { grouped ->
                val uiLang = appPrefs.getUiLanguage()
                suspend fun toUi(m: WordGroup): GroupUiDictionary {
                    val wordsCount = getWordsCountForGroup(m)
                    return GroupUiDictionary(
                        key = m.key,
                        titleKey = app.groupTitleRes(m.key),
                        iconKey = app.groupIconRes(m.key),
                        wordsInGroup = wordsCount
                    )
                }

                val userDomain = grouped.user.map { toUi(it) }
                val defaultDomain = grouped.defaults.map { toUi(it) }

                _categoriesGrouped.value = DictionaryScreenState(
                    userGroups = userDomain,
                    defaultGroups = defaultDomain
                )
            }
        }
    }
}
