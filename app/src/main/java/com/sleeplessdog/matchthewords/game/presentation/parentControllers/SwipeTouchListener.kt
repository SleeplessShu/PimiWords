package com.sleeplessdog.matchthewords.game.presentation.parentControllers

import android.view.MotionEvent
import android.view.View
import com.sleeplessdog.matchthewords.utils.ConstantsApp
import com.sleeplessdog.matchthewords.utils.ConstantsApp.SWIPE_VERTICAL_TRANSLATION_FACTOR
import kotlin.math.abs

class SwipeTouchListener(
    private val card: View,
    private val onSwipeRightCommit: () -> Unit,
    private val onSwipeLeftCommit: () -> Unit,
    private val canSwipe: () -> Boolean = { true }
) : View.OnTouchListener {

    private var downX = ConstantsApp.ZERO_SCALE
    private var downY = ConstantsApp.ZERO_SCALE
    private var isSwiping = false

    private val dragLimit = ConstantsApp.SWIPE_DRAG_LIMIT
    private val commitThreshold = ConstantsApp.SWIPE_COMMIT_THRESHOLD
    private val maxRotation = ConstantsApp.SWIPE_MAX_ROTATION

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        if (!canSwipe()) return false

        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                downX = event.rawX
                downY = event.rawY
                isSwiping = false
                return true
            }

            MotionEvent.ACTION_MOVE -> {
                val dx = event.rawX - downX
                val dy = event.rawY - downY
                if (!isSwiping && abs(dy) > abs(dx)) return false
                isSwiping = true

                val clamped = dx.coerceIn(-dragLimit, dragLimit)
                card.translationX = dx
                card.translationY = -abs(clamped) * SWIPE_VERTICAL_TRANSLATION_FACTOR
                card.rotation = (clamped / dragLimit) * maxRotation
                return true
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                if (!isSwiping) {
                    reset()
                    return false
                }
                val dx = event.rawX - downX
                val commitRight = dx > commitThreshold
                val commitLeft = dx < -commitThreshold

                when {
                    commitRight -> {
                        onSwipeRightCommit()
                    }

                    commitLeft -> {
                        onSwipeLeftCommit()
                    }

                    else -> reset()
                }
                return true
            }
        }
        return false
    }

    private fun reset() {
        card.animate()
            .translationX(ConstantsApp.ZERO_SCALE)
            .translationY(ConstantsApp.ZERO_SCALE)
            .rotation(ConstantsApp.ZERO_SCALE)
            .setDuration(ConstantsApp.SWIPE_RESET_DURATION_MS)
            .start()

    }
}
