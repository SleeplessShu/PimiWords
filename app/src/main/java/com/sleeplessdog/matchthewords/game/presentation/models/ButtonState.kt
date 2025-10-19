package com.sleeplessdog.matchthewords.game.presentation.models

import com.sleeplessdog.matchthewords.R

enum class ButtonState(
    val backgroundRes: Int,
    val textColorRes: Int,
    val enabled: Boolean
) {
    SELECTED(
        backgroundRes = R.drawable.cardwords_selected,
        textColorRes  = R.color.dark_text_selected, // яркий читаемый
        enabled       = true
    ),

    ERROR(
        backgroundRes = R.drawable.cardwords_error,
        textColorRes  = R.color.dark_text_selected, // яркий читаемый
        enabled       = false
    ),

    CORRECT(
        backgroundRes = R.drawable.cardwords_correct,
        textColorRes  = R.color.dark_text_selected,
        enabled       = false
    ),
    DISABLED(
        backgroundRes = R.drawable.cardwards_default,
        textColorRes  = R.color.dark_text_used,     // приглушённый
        enabled       = false
    ),
    DEFAULT(
        backgroundRes = R.drawable.cardwards_default,
        textColorRes  = R.color.dark_text_default,  // обычный
        enabled       = true
    )
}
