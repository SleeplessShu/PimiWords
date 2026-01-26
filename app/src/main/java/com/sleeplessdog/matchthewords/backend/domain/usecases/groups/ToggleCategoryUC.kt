package com.sleeplessdog.matchthewords.backend.domain.usecases.groups

import com.sleeplessdog.matchthewords.backend.data.repository.CategoriesRepository

class ToggleCategoryUC(
    private val repo: CategoriesRepository,
) {
    suspend operator fun invoke(key: String) {
        repo.toggle(key)
    }
}