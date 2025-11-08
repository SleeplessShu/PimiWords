package com.sleeplessdog.matchthewords.game.presentation.models

import com.sleeplessdog.matchthewords.R

enum class TOFButtonState(
    val backgroundRes: Int,
    val tintColorRes: Int,
    val offsetY: Float
) {
    DEFAULT(
        backgroundRes = R.drawable.cardwards_default,
        tintColorRes = android.R.color.transparent, // авто подставим в apply()
        offsetY = 0f
    ),
    PRESSED(
        backgroundRes = R.drawable.cardwards_default,
        tintColorRes = android.R.color.transparent,
        offsetY = 10f
    ),
    RESULT_CORRECT(
        backgroundRes = R.drawable.cardwords_correct,
        tintColorRes = R.color.dark_gray,
        offsetY = 0f
    ),
    RESULT_WRONG(
        backgroundRes = R.drawable.cardwords_error,
        tintColorRes = R.color.dark_gray,
        offsetY = 0f
    );
}