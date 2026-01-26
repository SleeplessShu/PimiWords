package com.sleeplessdog.matchthewords.backend.domain.usecases.groups

import com.sleeplessdog.matchthewords.backend.data.repository.GroupsRepository
import com.sleeplessdog.matchthewords.backend.domain.models.GroupedCategories
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ObserveAllGroupsGroupedUC(
    private val repo: GroupsRepository,
) {
    operator fun invoke(): Flow<GroupedCategories> = flow {
        val all = repo.getAllCategories()

        emit(
            GroupedCategories(
                user = all.filter { it.isUser },
                defaults = all.filter { !it.isUser }
            )
        )
    }
}
