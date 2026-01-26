package com.sleeplessdog.matchthewords.backend.domain.models

data class GroupedCategories(
    val user: List<WordGroup>,
    val defaults: List<WordGroup>,
)
