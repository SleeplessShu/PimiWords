package com.sleeplessdog.pimi.dictionary

import android.app.Application
import com.sleeplessdog.pimi.R
import com.sleeplessdog.pimi.games.domain.models.GroupDictionaryDomain
import com.sleeplessdog.pimi.games.domain.models.GroupUiDictionary
import com.sleeplessdog.pimi.utils.groupIconRes
import com.sleeplessdog.pimi.utils.groupTitleRes

class GroupDictionaryUiMapper(
    private val app: Application,
) {

    fun map(group: GroupDictionaryDomain): GroupUiDictionary {

        val isSavedWords = group.key == "saved_words"
        val isCustomUserGroup = group.isUser && !isSavedWords

        val titleRes = if (isCustomUserGroup) 0
        else app.groupTitleRes(group.key)


        val iconRes = when {
            isCustomUserGroup -> R.drawable.ic_group_default
            isSavedWords -> R.drawable.ic_group_saved
            else -> app.groupIconRes(group.key)
        }

        return GroupUiDictionary(
            key = group.key,
            title = group.title,
            titleRes = titleRes,
            wordsInGroup = group.wordsInGroup,
            iconRes = iconRes,
            isUser = group.isUser,
        )
    }
}