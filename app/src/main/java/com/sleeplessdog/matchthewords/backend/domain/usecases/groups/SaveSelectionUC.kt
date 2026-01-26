package com.sleeplessdog.matchthewords.backend.domain.usecases.groups

import com.sleeplessdog.matchthewords.backend.data.repository.GroupsRepository

class SaveSelectionUC(
    private val repo: GroupsRepository,
) {
    suspend operator fun invoke(keys: Set<String>) {
        repo.saveSelection(keys)
    }
}
