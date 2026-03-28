package com.sleeplessdog.pimi.score.models

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.sleeplessdog.pimi.score.domain.models.AwardId

data class AwardMeta(
    val id: AwardId = AwardId.I_UNDERSTOOD,
    @StringRes val title: Int,
    @StringRes val description: Int,
    @DrawableRes val iconLocked: Int,
    @DrawableRes val iconUnlocked: Int,
    val isLocked: Boolean = true,
)
