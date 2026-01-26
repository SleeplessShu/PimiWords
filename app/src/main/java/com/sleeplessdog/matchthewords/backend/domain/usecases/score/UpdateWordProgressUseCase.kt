package com.sleeplessdog.matchthewords.backend.domain.usecases.score

import com.sleeplessdog.matchthewords.backend.data.db.user.UserDao
import com.sleeplessdog.matchthewords.backend.data.db.user.UserWordProgressEntity
import com.sleeplessdog.matchthewords.utils.GamePrices.LEARNED_THRESHOLD

class UpdateWordProgressUseCase(
    private val userDao: UserDao,
) {

    suspend fun update(
        correctIds: List<Int>,
        mistakeIds: List<Int>,
    ) {
        val now = System.currentTimeMillis()

        // сначала ошибки (важно!)
        mistakeIds.forEach { id ->
            updateSingle(
                globalId = id.toLong(),
                isCorrect = false,
                now = now
            )
        }

        // потом правильные
        correctIds.forEach { id ->
            updateSingle(
                globalId = id.toLong(),
                isCorrect = true,
                now = now
            )
        }
    }

    private suspend fun updateSingle(
        globalId: Long,
        isCorrect: Boolean,
        now: Long,
    ) {
        val current = userDao.getProgress(globalId)

        val updated = if (current == null) {
            // первое появление слова
            if (isCorrect) {
                UserWordProgressEntity(
                    globalId = globalId,
                    correctCount = 1,
                    mistakeCount = 0,
                    lastSeenAt = now,
                    isLearned = 1 >= LEARNED_THRESHOLD
                )
            } else {
                UserWordProgressEntity(
                    globalId = globalId,
                    correctCount = 0,
                    mistakeCount = 1,
                    lastSeenAt = now,
                    isLearned = false
                )
            }
        } else {
            val newCorrect = when {
                isCorrect -> current.correctCount + 1
                else -> maxOf(0, current.correctCount - 1)
            }

            val newMistake =
                if (isCorrect) current.mistakeCount
                else current.mistakeCount + 1

            current.copy(
                correctCount = newCorrect,
                mistakeCount = newMistake,
                lastSeenAt = now,
                isLearned = newCorrect >= LEARNED_THRESHOLD
            )
        }

        userDao.saveProgress(updated)
    }
}
