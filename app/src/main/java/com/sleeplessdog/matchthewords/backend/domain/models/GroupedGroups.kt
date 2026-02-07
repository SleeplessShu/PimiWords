package com.sleeplessdog.matchthewords.backend.domain.models

data class GroupedGroups(
    val user: List<WordGroup>,
    val defaults: List<WordGroup>,
)
