package com.sleeplessdog.matchthewords.game.presentation.controller

import android.annotation.SuppressLint
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.ScrollView
import kotlin.math.max
import kotlin.math.min

class PimiScrollbarController(
    private val scrollView: ScrollView,
    private val track: ImageView,
    private val thumb: ImageView,
) {
    private var isDragging = false
    private var touchOffsetY = 0f

    fun attach() {

        track.post { updateThumbFromScroll() }

        scrollView.viewTreeObserver.addOnScrollChangedListener {
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

    private fun scrollRange(): Int {
        val content = scrollView.getChildAt(0) ?: return 0
        return max(0, content.height - scrollView.height)
    }

    private fun thumbTravelRange(): Int {
        return max(0, track.height - thumb.height)
    }

    private fun updateThumbFromScroll() {
        val range = scrollRange()
        val travel = thumbTravelRange()

        if (range == 0 || travel == 0) {
            thumb.translationY = 0f
            thumb.visibility = View.GONE
            return
        }
        thumb.visibility = View.VISIBLE

        val progress = scrollView.scrollY.toFloat() / range.toFloat()
        val y = travel * progress
        thumb.translationY = clamp(y, 0f, travel.toFloat())
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupDrag() {
        val trackLoc = IntArray(2)

        thumb.setOnTouchListener { _, ev ->
            val range = scrollRange()
            val travel = thumbTravelRange()
            if (range == 0 || travel == 0) return@setOnTouchListener false

            when (ev.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    isDragging = true
                    track.getLocationOnScreen(trackLoc)
                    val trackTopOnScreen = trackLoc[1].toFloat()
                    
                    touchOffsetY = ev.rawY - (trackTopOnScreen + thumb.translationY)
                    thumb.parent.requestDisallowInterceptTouchEvent(true)
                    true
                }

                MotionEvent.ACTION_MOVE -> {
                    val trackTopOnScreen = trackLoc[1].toFloat()
                    val targetInsideTrack = ev.rawY - trackTopOnScreen - touchOffsetY

                    val clamped = clamp(targetInsideTrack, 0f, travel.toFloat())
                    thumb.translationY = clamped

                    val progress = clamped / travel.toFloat()
                    val targetScroll = (progress * range).toInt()
                    scrollView.scrollTo(0, targetScroll)
                    true
                }

                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    isDragging = false
                    thumb.parent.requestDisallowInterceptTouchEvent(false)
                    true
                }

                else -> false
            }
        }
    }

    private fun clamp(v: Float, minV: Float, maxV: Float): Float =
        max(minV, min(maxV, v))

    private companion object {
        private val TRACK_PADDING_VERTICAL_PX: Int = 40
    }
}