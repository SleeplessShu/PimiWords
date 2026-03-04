package com.sleeplessdog.matchthewords.dictionary.dictionary_screen

import com.sleeplessdog.matchthewords.dictionary.group_screen.GroupType

object DictionaryDestinations {
    const val MAIN = "dictionary_main"
    const val GROUP = "dictionary_group"

    const val ARG_GROUP_ID = "groupId"
    const val ARG_GROUP_NAME = "groupName"
    const val ARG_GROUP_TYPE = "groupType"
//    const val ARG_GROUP_USER = "isUser"

    fun groupRoute(
        groupId: String,
        groupName: String,
        type: GroupType,
    ) = "$GROUP/$groupId/$groupName/${type.name}"
}