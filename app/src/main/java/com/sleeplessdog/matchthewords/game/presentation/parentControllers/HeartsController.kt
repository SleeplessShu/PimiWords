package com.sleeplessdog.matchthewords.game.presentation.parentControllers

import android.widget.ImageView
import androidx.core.view.isVisible
import com.airbnb.lottie.LottieAnimationView
import com.sleeplessdog.matchthewords.R
import com.sleeplessdog.matchthewords.game.presentation.parentModels.HeartState
import com.sleeplessdog.matchthewords.game.presentation.parentModels.StaticHeartState

class HeartsController(
     val hearts: List<LottieAnimationView>
) {
    private val staticHeartState: Map<Int, List<StaticHeartState>> = mapOf(
        3 to listOf(
            StaticHeartState.BRIGHT_ORANGE,
            StaticHeartState.BRIGHT_ORANGE,
            StaticHeartState.BRIGHT_ORANGE
        ),
        2 to listOf(
            StaticHeartState.MEDIUM_ORANGE,
            StaticHeartState.MEDIUM_ORANGE,
            StaticHeartState.HIDDEN
        ),
        1 to listOf(
            StaticHeartState.DARK_ORANGE,
            StaticHeartState.HIDDEN,
            StaticHeartState.HIDDEN
        ),
        0 to listOf(
            StaticHeartState.HIDDEN,
            StaticHeartState.HIDDEN,
            StaticHeartState.HIDDEN
        )
    )

    private var prevHeartsQuantity: Int = 0

    // Маппинг из статичного состояния в анимированное или null (если hidden)
    private fun StaticHeartState.toHeartState(): HeartState? = when (this) {
        StaticHeartState.DARK_ORANGE -> HeartState.FAST_DARK_TO_MEDIUM
        StaticHeartState.MEDIUM_ORANGE -> HeartState.APPEAR_MEDIUM_ORANGE
        StaticHeartState.BRIGHT_ORANGE -> HeartState.APPEAR_BRIGHT_ORANGE
        StaticHeartState.HIDDEN -> null
    }

    /* fun render(heartsQuantity: Int) {
         val states = staticHeartState[heartsQuantity.coerceIn(0, 3)] ?: return
         hearts.zip(states).forEach { (lav, st) -> applyHeartAnimated(lav = lav, state = st) }
     }*/

    fun render(heartsQuantity: Int) {
        val prevStates = staticHeartState[prevHeartsQuantity.coerceIn(0, 3)] ?: return
        val currStates = staticHeartState[heartsQuantity.coerceIn(0, 3)] ?: return

        hearts.zip(currStates.withIndex()).forEach { (lav, indexed) ->
            val (index, currStaticState) = indexed
            val prevStaticState = prevStates.getOrNull(index) ?: StaticHeartState.HIDDEN

            val heartState = determineHeartState(prevStaticState, currStaticState)
            if (heartState == null) {
                lav.cancelAnimation()
                lav.isVisible = false
            } else {
                lav.isVisible = true
                applyHeartAnimated(lav, heartState)
            }
        }

        prevHeartsQuantity = heartsQuantity.coerceIn(0, 3)
    }

    private fun determineHeartState(
        prev: StaticHeartState,
        curr: StaticHeartState
    ): HeartState? {
        return when {
            curr == StaticHeartState.HIDDEN -> null

            prev == StaticHeartState.HIDDEN && curr == StaticHeartState.MEDIUM_ORANGE -> HeartState.APPEAR_MEDIUM_ORANGE
            prev == StaticHeartState.HIDDEN && curr == StaticHeartState.BRIGHT_ORANGE -> HeartState.APPEAR_BRIGHT_ORANGE

            prev == StaticHeartState.DARK_ORANGE && curr == StaticHeartState.MEDIUM_ORANGE -> HeartState.FAST_DARK_TO_MEDIUM
            prev == StaticHeartState.MEDIUM_ORANGE && curr == StaticHeartState.BRIGHT_ORANGE -> HeartState.MEDIUM_TO_BRIGHT

            // Если состояние не меняется, проигрываем статичную анимацию по текущему состоянию
            curr == StaticHeartState.DARK_ORANGE -> HeartState.FAST_DARK_TO_MEDIUM
            curr == StaticHeartState.MEDIUM_ORANGE -> HeartState.APPEAR_MEDIUM_ORANGE
            curr == StaticHeartState.BRIGHT_ORANGE -> HeartState.APPEAR_BRIGHT_ORANGE

            else -> null
        }
    }


    private fun applyHeartAnimated(lav: LottieAnimationView, state: HeartState) {
        when (state) {
            HeartState.FAST_DARK_TO_MEDIUM -> lav.setAnimation(R.raw.animation_becomes)
            HeartState.APPEAR_MEDIUM_ORANGE -> lav.setAnimation(R.raw.animation_become)
            HeartState.MEDIUM_TO_BRIGHT -> lav.setAnimation(R.raw.animation_get_heart_to_two)
            HeartState.APPEAR_BRIGHT_ORANGE -> lav.setAnimation(R.raw.animation_get_heart_to_three)
        }
        lav.playAnimation()
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