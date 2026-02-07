package com.sleeplessdog.matchthewords.dictionary

data class GroupUiDictionary(
    val key: String,
    val titleKey: Int,
    val wordsInGroup: Int,
    val iconKey: Int,
)

data class DictionaryScreenState(
    val userGroups: List<GroupUiDictionary>,
    val defaultGroups: List<GroupUiDictionary>,
)
