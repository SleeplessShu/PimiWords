package com.sleeplessdog.matchthewords.game.presentation.parentControllers

import androidx.core.view.isVisible
import com.airbnb.lottie.LottieAnimationView
import com.sleeplessdog.matchthewords.R
import com.sleeplessdog.matchthewords.game.presentation.parentModels.HeartState
import com.sleeplessdog.matchthewords.game.presentation.parentModels.StaticHeartState

/**
 * Управляет визуальным отображением «жизней» (сердечек) в игре с использованием Lottie‑анимаций.
 * Меняет состояние сердечек (цвет, видимость, анимации) в зависимости от текущего количества жизней.
 */
class HeartsController(
    private val hearts: List<LottieAnimationView>,
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

    /**
     * Обновляет отображение сердечек согласно текущему количеству жизней.
     */
    fun render(heartsQuantity: Int) {
        val prevStates = staticHeartState[prevHeartsQuantity.coerceIn(0, 3)] ?: return
        val currStates = staticHeartState[heartsQuantity.coerceIn(0, 3)] ?: return

        hearts.zip(currStates.withIndex()).forEach { (lav, indexed) ->
            val (index, currStaticState) = indexed
            val prevStaticState = prevStates.getOrNull(index) ?: StaticHeartState.HIDDEN

            val heartState = determineHeartState(prevStaticState, currStaticState)
            if (heartState != null) {
                lav.isVisible = true
                applyHeartAnimated(lav, heartState)
            }
        }
        prevHeartsQuantity = heartsQuantity.coerceIn(0, 3)
    }

    /**
     * Определяет анимационное состояние сердечка на основе перехода между предыдущим и текущим статическим состоянием.
     */
    private fun determineHeartState(
        prev: StaticHeartState,
        curr: StaticHeartState
    ): HeartState? {
        return when {

            prev == StaticHeartState.BRIGHT_ORANGE && curr == StaticHeartState.MEDIUM_ORANGE -> HeartState.BRIGHT_TO_MEDIUM
            prev == StaticHeartState.BRIGHT_ORANGE && curr == StaticHeartState.BRIGHT_ORANGE -> null
            prev == StaticHeartState.BRIGHT_ORANGE && curr == StaticHeartState.HIDDEN -> HeartState.DEAD_BRIGHT_ORANGE
            prev == StaticHeartState.MEDIUM_ORANGE && curr == StaticHeartState.DARK_ORANGE -> HeartState.MEDIUM_TO_DARK
            prev == StaticHeartState.MEDIUM_ORANGE && curr == StaticHeartState.HIDDEN -> HeartState.DEAD_MEDIUM
            prev == StaticHeartState.DARK_ORANGE && curr == StaticHeartState.HIDDEN -> HeartState.DEAD_DARK

            prev == StaticHeartState.DARK_ORANGE && curr == StaticHeartState.MEDIUM_ORANGE -> HeartState.DARK_TO_MEDIUM
            prev == StaticHeartState.HIDDEN && curr == StaticHeartState.MEDIUM_ORANGE -> HeartState.APPEAR_MEDIUM_ORANGE
            prev == StaticHeartState.MEDIUM_ORANGE && curr == StaticHeartState.BRIGHT_ORANGE -> HeartState.MEDIUM_TO_BRIGHT

            curr == StaticHeartState.BRIGHT_ORANGE -> HeartState.APPEAR_BRIGHT_ORANGE
            else -> null
        }
    }

    /**
     *  Применяет Lottie‑анимацию к указанному представлению сердечка.
     */
    private fun applyHeartAnimated(
        lav: LottieAnimationView,
        state: HeartState,
    ) {
        when (state) {
            HeartState.APPEAR_BRIGHT_ORANGE -> lav.setAnimation(R.raw.animation_get_three_hearts)
            HeartState.BRIGHT_TO_MEDIUM -> lav.setAnimation(R.raw.animation_from_three_lives_into_two)
            HeartState.DEAD_BRIGHT_ORANGE -> lav.setAnimation(R.raw.animation_disappearance_bright_heart)
            HeartState.MEDIUM_TO_DARK -> lav.setAnimation(R.raw.animation_from_two_lives_into_one)
            HeartState.DEAD_MEDIUM -> lav.setAnimation(R.raw.animation_disappearance_middle_heart)
            HeartState.DEAD_DARK -> lav.setAnimation(R.raw.animation_disappearance_dark_heart)
            HeartState.DARK_TO_MEDIUM -> lav.setAnimation(R.raw.animation_from_one_life_into_two)
            HeartState.APPEAR_MEDIUM_ORANGE -> lav.setAnimation(R.raw.animation_get_two_hearts)
            HeartState.MEDIUM_TO_BRIGHT -> lav.setAnimation(R.raw.animation_from_two_lives_into_three)
        }
        lav.playAnimation()
    }
}
