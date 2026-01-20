package com.sleeplessdog.matchthewords.dictionary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sleeplessdog.matchthewords.game.domain.repositories.WordCategoriesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class DictionaryViewModel(
    private val repository: WordCategoriesRepository
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
            val allCategories = repository.observeAll().first()
            val userGroups = allCategories.filter { it.isUser }.map { category ->
                MyGroup(
                    myGroupName = category.titleKey,
                    countWords = 1,
                    iconItem = category.iconKey
                )
            }

            val defaultGroups = allCategories.filter { !it.isUser }.map { category ->
                StandardGroup(
                    standardGroupName = category.titleKey,
                    countWords = 1,
                    iconItem = category.iconKey
                )
            }

            _categoriesGrouped.value = DictionaryScreenState(
                userGroups = userGroups,
                defaultGroups = defaultGroups
            )
        }
    }
}