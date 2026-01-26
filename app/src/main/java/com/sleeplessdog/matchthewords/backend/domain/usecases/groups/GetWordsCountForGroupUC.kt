package com.sleeplessdog.matchthewords.backend.domain.usecases.groups

import com.sleeplessdog.matchthewords.backend.data.repository.GroupsRepository
import com.sleeplessdog.matchthewords.backend.domain.models.WordGroup

class GetWordsCountForGroupUC(
    private val repository: GroupsRepository,
) {
    suspend operator fun invoke(group: WordGroup): Int {
        return repository.getWordsCount(group)
    }
}
