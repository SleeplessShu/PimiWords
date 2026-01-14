package com.sleeplessdog.matchthewords.score.models

data class Award(
    val title: String = "",
    val description: String = "",
    val imagePath: Int = 0,
    val isLocked: Boolean = true,
)
