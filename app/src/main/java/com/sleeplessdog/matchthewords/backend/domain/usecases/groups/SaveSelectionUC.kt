package com.sleeplessdog.matchthewords.backend.domain.usecases.groups

import com.sleeplessdog.matchthewords.backend.data.repository.CategoriesRepository

class SaveSelectionUC(
    private val repo: CategoriesRepository,
) {
    suspend operator fun invoke(keys: Set<String>) {
        repo.saveSelection(keys)
    }
}
