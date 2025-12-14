package com.sleeplessdog.matchthewords.game.presentation.parentControllers

import android.widget.ImageView
import androidx.core.view.isVisible
import com.sleeplessdog.matchthewords.R
import com.sleeplessdog.matchthewords.game.presentation.parentModels.HeartState

class HeartsController(
    private val hearts: List<ImageView>
) {
    private val HEART_STATES: Map<Int, List<HeartState>> = mapOf(
        3 to listOf(HeartState.FULL, HeartState.FULL, HeartState.FULL),
        2 to listOf(HeartState.HALF, HeartState.HALF, HeartState.HIDDEN),
        1 to listOf(HeartState.LOW, HeartState.HIDDEN, HeartState.HIDDEN),
        0 to listOf(HeartState.HIDDEN, HeartState.HIDDEN, HeartState.HIDDEN)
    )

    fun render(heartsQuantity: Int) {
        val states = HEART_STATES[heartsQuantity.coerceIn(0, 3)] ?: return
        hearts.zip(states).forEach { (iv, st) -> applyHeartAnimated(iv, st) }
    }

    private fun applyHeartAnimated(iv: ImageView, state: HeartState) {
        when (state) {
            HeartState.FULL -> showHeart(iv, R.drawable.int_lives_3)
            HeartState.HALF -> showHeart(iv, R.drawable.int_lives_2)
            HeartState.LOW  -> showHeart(iv, R.drawable.int_lives_1)
            HeartState.HIDDEN -> hideHeart(iv)
        }
    }

    private fun showHeart(iv: ImageView, res: Int) {
        iv.setImageResource(res)
        if (!iv.isVisible) {
            iv.alpha = 0f
            iv.isVisible = true
            iv.animate()
                .alpha(1f)
                .setDuration(250)
                .start()
        }
    }

    private fun hideHeart(iv: ImageView) {
        if (iv.isVisible) {
            iv.animate()
                .alpha(0f)
                .setDuration(200)
                .withEndAction { iv.isVisible = false }
                .start()
        }
    }
}