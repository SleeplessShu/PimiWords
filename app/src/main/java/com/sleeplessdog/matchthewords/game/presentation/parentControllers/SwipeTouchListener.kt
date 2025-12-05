package com.sleeplessdog.matchthewords.game.presentation.parentControllers

import android.graphics.drawable.Drawable
import android.view.MotionEvent
import android.view.View
import androidx.core.graphics.drawable.DrawableCompat
import com.sleeplessdog.matchthewords.R
import kotlin.math.abs

class SwipeTouchListener(
    private val card: View,
    private val wouldBeCorrect: (isRightSwipe: Boolean) -> Boolean,
    private val onSwipeRightCommit: () -> Unit,
    private val onSwipeLeftCommit: () -> Unit,
    private val canSwipe: () -> Boolean = { true }
) : View.OnTouchListener {

    private var downX = 0f
    private var downY = 0f
    private var isSwiping = false

    private val dragLimit = 280f
    private val commitThreshold = 140f
    private val maxRotation = 15f

    private var originalBackground: Drawable? = null

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
                card.translationY = -abs(clamped) * 0.12f
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
                val commitLeft  = dx < -commitThreshold

                when {
                    commitRight -> {
                        // НЕ красим и НЕ анимируем тут — фрагмент сделает всё сам
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
            .translationX(0f)
            .translationY(0f)
            .rotation(0f)
            .setDuration(180)
            .start()
    }

    private fun finishSwipe(isRight: Boolean) {
        val correct = wouldBeCorrect(isRight)

        val color = if (correct)
            card.context.getColor(R.color.green_primary)
        else
            card.context.getColor(R.color.red_primary)

        // окрашиваем только теперь
        tintCard(color)

        val dir = if (isRight) 1 else -1
        card.animate()
            .translationX(dir * card.width.toFloat())
            .translationY(-card.height * 0.35f)
            .rotation(dir * 35f)
            .alpha(0f)
            .setDuration(200)
            .withEndAction {
                card.translationX = 0f
                card.translationY = 0f
                card.rotation = 0f
                card.alpha = 1f
                restoreCardBackground()

                if (isRight) onSwipeRightCommit() else onSwipeLeftCommit()
            }
            .start()
    }



    private fun tintCard(color: Int) {
        val d = card.background?.mutate() ?: return
        DrawableCompat.setTint(d, color)
        card.background = d
    }

    private fun restoreCardBackground() {
        card.background = originalBackground
    }
}