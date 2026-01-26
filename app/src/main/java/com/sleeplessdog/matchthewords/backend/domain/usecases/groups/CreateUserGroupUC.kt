package com.sleeplessdog.matchthewords.backend.domain.usecases.groups

import com.sleeplessdog.matchthewords.backend.data.repository.CategoriesRepository

class CreateUserGroupUC(
    private val repo: CategoriesRepository,
) {
    suspend operator fun invoke(
        key: String,
        titleKey: String,
        iconKey: String,
    ) {
        repo.createUserCategory(key, titleKey, iconKey)
    }
}
