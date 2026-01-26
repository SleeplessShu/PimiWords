package com.sleeplessdog.matchthewords.backend.domain.usecases.words

import com.sleeplessdog.matchthewords.backend.data.db.user.UserDao
import com.sleeplessdog.matchthewords.backend.data.db.user.UserWordEntity
import com.sleeplessdog.matchthewords.utils.ConstantsPaths

class AddWordToUserDictionaryUC(
    private val userDao: UserDao,
) {

    suspend fun add(globalIds: List<Int>) {
        if (globalIds.isEmpty()) return

        val savedGroup = userDao.getGroupByKey(ConstantsPaths.SAVED_GROUP_KEY)
            ?: error("Saved words group not found")

        // 2. сохраняем слова
        globalIds
            .distinct()
            .forEach { id ->
                val globalId = id.toLong()

                // если слово уже сохранено — пропускаем
                val exists = userDao.findByGlobalId(globalId)
                if (exists != null) return@forEach

                userDao.insertWord(
                    UserWordEntity(
                        globalId = globalId,
                        groupId = savedGroup.id,
                        english = null,
                        spanish = null,
                        russian = null,
                        french = null,
                        german = null,
                        armenian = null,
                        serbian = null
                    )
                )
            }
    }

    private companion object {

    }
}