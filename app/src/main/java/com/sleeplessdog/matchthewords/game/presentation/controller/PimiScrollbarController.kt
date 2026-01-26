package com.sleeplessdog.matchthewords.game.presentation.controller

import android.annotation.SuppressLint
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import com.sleeplessdog.matchthewords.game.presentation.view.PimiScrollable
import kotlin.math.max
import kotlin.math.min

class PimiScrollbarController(
    private val scrollable: PimiScrollable,
    private val track: ImageView,
    private val thumb: ImageView,
) {
    private var isDragging = false

    fun attach() {
        track.post { updateThumbFromScroll() }

        scrollable.addOnScrollListener {
            if (!isDragging) updateThumbFromScroll()
        }

        track.addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
            if (!isDragging) updateThumbFromScroll()
        }
        thumb.addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
            if (!isDragging) updateThumbFromScroll()
        }

        setupDrag()
    }

    private fun scrollRange(): Int = scrollable.getScrollRange()

    private fun thumbTravelRange(): Int {
        return max(0, track.height - thumb.height)
    }

    private fun updateThumbFromScroll() {
        val range = scrollRange()
        val travel = thumbTravelRange()

        if (range <= 0 || travel <= 0) {
            thumb.translationY = 0f
            return
        }
        thumb.visibility = View.VISIBLE

        val progress = scrollable.currentScrollY.toFloat() / range.toFloat()
        val y = travel * progress
        thumb.translationY = clamp(y, 0f, travel.toFloat())
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupDrag() {
        lateinit var trackLoc: IntArray
        var touchOffsetY = 0f

        thumb.setOnTouchListener { _, ev ->
            val range = scrollRange()
            val travel = thumbTravelRange()
            if (range == 0 || travel == 0) return@setOnTouchListener false

            when (ev.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    isDragging = true
                    trackLoc = IntArray(2)
                    track.getLocationOnScreen(trackLoc)
                    val trackTopOnScreen = trackLoc[1].toFloat()
                    touchOffsetY = ev.rawY - (trackTopOnScreen + thumb.translationY)

                    scrollable.view.parent.requestDisallowInterceptTouchEvent(true)
                    true
                }

                MotionEvent.ACTION_MOVE -> {
                    if (!isDragging) return@setOnTouchListener false
                    val trackTopOnScreen = trackLoc[1].toFloat()
                    val targetInsideTrack = ev.rawY - trackTopOnScreen - touchOffsetY
                    val clamped = clamp(targetInsideTrack, 0f, travel.toFloat())
                    thumb.translationY = clamped
                    val progress = clamped / travel.toFloat()
                    val targetScroll = (progress * range).toInt()

                    scrollable.scrollTo(targetScroll)
                    true
                }

                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    isDragging = false
                    scrollable.view.parent.requestDisallowInterceptTouchEvent(false)
                    true
                }

                else -> false
            }
        }
    }

    fun forceUpdate() {
        track.post {
            updateThumbFromScroll()
        }
    }

    private fun clamp(v: Float, minV: Float, maxV: Float): Float = max(minV, min(maxV, v))
}
