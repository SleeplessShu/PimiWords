package com.sleeplessdog.matchthewords.dictionary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sleeplessdog.matchthewords.game.domain.repositories.CategoriesGrouped
import com.sleeplessdog.matchthewords.game.domain.repositories.WordCategoriesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class DictionaryViewModel(
    private val repository: WordCategoriesRepository
) : ViewModel() {

    /*  private val _groupState = MutableStateFlow(DictionaryWordGroups.BOTH_PARTIALLY)
      val groupState: StateFlow<DictionaryWordGroups> = _groupState*/

    private val _categoriesGrouped = MutableStateFlow(
        CategoriesGrouped(user = emptyList(), defaults = emptyList())
    )
    val categoriesGrouped: StateFlow<CategoriesGrouped> = _categoriesGrouped

    fun setGroup() {
        viewModelScope.launch {
            // Получаем все категории из репозитория (Flow -> first)
            val allCategories = repository.observeAll().first()

            // Обновляем состояние разделённых категорий
            _categoriesGrouped.value = CategoriesGrouped(
                user = allCategories.filter { it.isUser },
                defaults = allCategories.filter { !it.isUser }
            )
        }
    }


}