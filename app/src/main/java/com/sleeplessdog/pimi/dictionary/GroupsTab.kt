package com.sleeplessdog.pimi.dictionary

import androidx.annotation.StringRes
import com.sleeplessdog.matchthewords.R

enum class GroupsTab(
    val route: String,
    @StringRes val label: Int,
) {
    USERS("groups_users", R.string.dictionary_my_groups),
    GLOBAL("groups_global", R.string.dictionary_default_groups)
}