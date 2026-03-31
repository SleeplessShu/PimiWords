package com.sleeplessdog.pimi.dictionary.models

import android.app.Application
import com.sleeplessdog.matchthewords.R
import com.sleeplessdog.pimi.games.domain.models.GroupUiSettings
import com.sleeplessdog.pimi.games.domain.models.WordGroup
import com.sleeplessdog.pimi.utils.ConstantsPaths.SAVED_GROUP_KEY
import com.sleeplessdog.pimi.utils.groupIconRes
import com.sleeplessdog.pimi.utils.groupTitleRes

class GroupSettingsUiMapper(
    private val app: Application,
) {

    fun map(group: WordGroup): GroupUiSettings {

        val isSavedWords = group.key == SAVED_GROUP_KEY
        val isCustomUserGroup = group.isUser && !isSavedWords

        val titleRes = if (isCustomUserGroup) 0
        else app.groupTitleRes(group.key)

        val title = if (isCustomUserGroup) group.title
        else null

        val iconRes = when {
            isCustomUserGroup -> R.drawable.ic_group_default
            isSavedWords -> R.drawable.ic_group_saved
            else -> app.groupIconRes(group.key)
        }

        return GroupUiSettings(
            key = group.key,
            titleRes = titleRes,
            title = title,
            iconRes = iconRes,
            isSelected = group.isSelected,
            isUser = group.isUser,
            orderInBlock = group.orderInBlock
        )
    }
}