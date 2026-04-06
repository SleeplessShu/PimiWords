package com.sleeplessdog.pimi.games.presentation.models

import com.sleeplessdog.pimi.R

enum class ButtonState(
    val backgroundRes: Int,
    val textColorRes: Int,
    val enabled: Boolean,
) {
    SELECTED(
        backgroundRes = R.drawable.bg_card_selected,
        textColorRes = R.color.dark_text_selected,
        enabled = true
    ),

    ERROR(
        backgroundRes = R.drawable.bg_card_error,
        textColorRes = R.color.dark_text_selected,
        enabled = false
    ),

    CORRECT(
        backgroundRes = R.drawable.bg_card_correct,
        textColorRes = R.color.dark_text_selected,
        enabled = false
    ),
    DISABLED(
        backgroundRes = R.drawable.bg_card_default,
        textColorRes = R.color.dark_text_used,
        enabled = false
    ),
    DEFAULT(
        backgroundRes = R.drawable.bg_card_default,
        textColorRes = R.color.dark_text_default,
        enabled = true
    )
}
