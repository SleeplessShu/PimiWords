package com.sleeplessdog.matchthewords.backend.domain.usecases.groups

import com.sleeplessdog.matchthewords.backend.data.repository.CategoriesRepository
import com.sleeplessdog.matchthewords.backend.domain.models.WordGroup
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ObserveFeaturedGroupsUC(
    private val repo: CategoriesRepository,
) {
    operator fun invoke(limit: Int): Flow<List<WordGroup>> = flow {
        emit(repo.getAllCategories().take(limit))
    }
}
