package com.sleeplessdog.pimi.dictionary.group_screen

data class GroupScreenState(
    val groupType: GroupType = GroupType.GLOBAL,
    val groupId: String = "",
    val groupTitle: String = "",
    val groupTitleRes: Int = 0,
    val words: List<WordUi> = emptyList(),
    val wordsCount: Int = 0,
    val groups: List<UserGroupShort> = emptyList(),
    val loading: Boolean = true,
)