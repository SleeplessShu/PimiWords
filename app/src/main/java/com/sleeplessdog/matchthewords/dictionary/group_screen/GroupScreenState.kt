package com.sleeplessdog.matchthewords.dictionary.group_screen

import com.sleeplessdog.matchthewords.dictionary.models.UserGroupShort

data class GroupScreenState(
    val groupType: GroupType = GroupType.GLOBAL,
    val groupId: String = "",
    val groupTitle: String = "",
    val words: List<WordUi> = emptyList(),
    val wordsCount: Int = 0,
    val groups: List<UserGroupShort> = emptyList(),
    val loading: Boolean = true,
)