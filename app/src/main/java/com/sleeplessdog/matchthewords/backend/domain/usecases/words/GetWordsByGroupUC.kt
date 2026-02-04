package com.sleeplessdog.matchthewords.backend.domain.usecases.words

import com.sleeplessdog.matchthewords.backend.data.db.user.UserWordEntity
import com.sleeplessdog.matchthewords.backend.data.repository.WordsRepository

class GetWordsByGroupUC(
    private val repository: WordsRepository
) {
    suspend fun getWordsByGroup(groupKey: String): List<UserWordEntity> {
        return repository.getWordsByGroup(groupKey)
    }
}