package com.sleeplessdog.matchthewords.game.presentation.models

data class LandingConditions(
    val shouldShow: Boolean = false,
    val headerTextId: Int = 0,
    val regularTextId: Int = 0,
    val animation: Int = 0,
    val key: LandingKeys = LandingKeys.APP_FIRST_LAUNCH,
)
