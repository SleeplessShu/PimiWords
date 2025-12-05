package com.sleeplessdog.matchthewords.game.presentation.models

import com.sleeplessdog.matchthewords.R

enum class ButtonState(
    val backgroundRes: Int,
    val textColorRes: Int,
    val enabled: Boolean
) {
    SELECTED(
        backgroundRes = R.drawable.bg_card_selected,
        textColorRes  = R.color.dark_text_selected, // яркий читаемый
        enabled       = true
    ),

    ERROR(
        backgroundRes = R.drawable.bg_card_error,
        textColorRes  = R.color.dark_text_selected, // яркий читаемый
        enabled       = false
    ),

    CORRECT(
        backgroundRes = R.drawable.bg_card_correct,
        textColorRes  = R.color.dark_text_selected,
        enabled       = false
    ),
    DISABLED(
        backgroundRes = R.drawable.bg_card_default,
        textColorRes  = R.color.dark_text_used,     // приглушённый
        enabled       = false
    ),
    DEFAULT(
        backgroundRes = R.drawable.bg_card_default,
        textColorRes  = R.color.dark_text_default,  // обычный
        enabled       = true
    )
}
