package com.sleeplessdog.matchthewords.game.presentation.models

data class GroupUiSettings(
    val key: String,
    val titleRes: Int,
    val iconRes: Int,
    val isSelected: Boolean,
    val isUser: Boolean,
)