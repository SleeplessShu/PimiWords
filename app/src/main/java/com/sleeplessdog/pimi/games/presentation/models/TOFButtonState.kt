package com.sleeplessdog.pimi.games.presentation.models

import com.sleeplessdog.pimi.R

enum class TOFButtonState(
    val backgroundRes: Int,
    val tintColorRes: Int,
    val offsetY: Float,
) {
    DEFAULT(
        backgroundRes = R.drawable.bg_card_default,
        tintColorRes = android.R.color.transparent,
        offsetY = 0f
    ),
    PRESSED(
        backgroundRes = R.drawable.bg_card_default,
        tintColorRes = android.R.color.transparent,
        offsetY = 10f
    ),
    RESULT_CORRECT(
        backgroundRes = R.drawable.bg_card_correct, tintColorRes = R.color.dark_gray, offsetY = 0f
    ),
    RESULT_WRONG(
        backgroundRes = R.drawable.bg_card_error, tintColorRes = R.color.dark_gray, offsetY = 0f
    );
}