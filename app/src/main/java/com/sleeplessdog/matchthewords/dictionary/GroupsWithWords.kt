package com.sleeplessdog.matchthewords.dictionary

data class MyGroup(
    val myGroupName: String,
    val countWords: Int,
    val iconItem: String
)

data class StandardGroup(
    val standardGroupName: String,
    val countWords: Int,
    val iconItem: String
)

data class DictionaryScreenState(
    val userGroups: List<MyGroup>,
    val defaultGroups: List<StandardGroup>
)
