package com.sleeplessdog.matchthewords.game.presentation.models

data class GroupsUiState(
    val featured: List<GroupUiSettings> = emptyList(),
    val user: List<GroupUiSettings> = emptyList(),
    val defaults: List<GroupUiSettings> = emptyList(),
    val loading: Boolean = true,
    val error: Throwable? = null,
)