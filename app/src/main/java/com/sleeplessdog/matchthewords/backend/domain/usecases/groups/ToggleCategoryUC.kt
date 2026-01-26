package com.sleeplessdog.matchthewords.backend.domain.usecases.groups

import com.sleeplessdog.matchthewords.backend.data.repository.GroupsRepository

class ToggleCategoryUC(
    private val repo: GroupsRepository,
) {
    suspend operator fun invoke(key: String) {
        repo.toggle(key)
    }
}