package com.sleeplessdog.matchthewords.backend.domain.usecases.groups

import com.sleeplessdog.matchthewords.backend.data.db.user.UserDao

class GetSelectedGroupsUC(
    private val userDao: UserDao,
) {
    suspend fun get(): Set<String> {
        val raw = userDao.getSelectedGroups()

        return raw
            ?.split(",")
            ?.map { it.trim() }
            ?.filter { it.isNotEmpty() }
            ?.toSet()
            ?: emptySet()
    }
}