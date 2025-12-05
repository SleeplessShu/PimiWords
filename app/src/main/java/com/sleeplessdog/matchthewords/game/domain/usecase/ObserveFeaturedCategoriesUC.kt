package com.sleeplessdog.matchthewords.game.domain.usecase

import com.sleeplessdog.matchthewords.game.domain.models.WordCategory
import com.sleeplessdog.matchthewords.game.domain.repositories.WordCategoriesRepository
import kotlinx.coroutines.flow.first

class ObserveFeaturedCategoriesUC(private val repo: WordCategoriesRepository) {
    operator fun invoke(limit: Int) = repo.observeFeatured(limit)
}

class ObserveAllCategoriesGroupedUC(private val repo: WordCategoriesRepository) {
    operator fun invoke() = repo.observeAllGrouped()
}

class ToggleCategoryUC(private val repo: WordCategoriesRepository) {
    suspend operator fun invoke(key: String) = repo.toggleSelection(key)
}

class SaveSelectionUC(private val repo: WordCategoriesRepository) {
    suspend operator fun invoke(keys: Set<String>) = repo.saveSelection(keys)
}

class CreateUserCategoryUC(private val repo: WordCategoriesRepository) {
    suspend operator fun invoke(key: String, titleKey: String, iconKey: String) =
        repo.upsertUserCategory(key, titleKey, iconKey)
}

class DeleteUserCategoryUC(private val repo: WordCategoriesRepository) {
    suspend operator fun invoke(key: String) = repo.deleteUserCategory(key)
}

class GetSelectedCategoriesUC(
    private val repo: WordCategoriesRepository
) {
    suspend operator fun invoke(): List<WordCategory> {
        val grouped = repo.observeAllGrouped().first()
        val all = grouped.user + grouped.defaults

        return all.filter { it.isSelected }
    }
}



