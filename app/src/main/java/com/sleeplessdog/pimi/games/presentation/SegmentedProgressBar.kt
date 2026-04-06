package com.sleeplessdog.pimi.games.presentation

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import com.sleeplessdog.pimi.R
import java.lang.Math.max
import kotlin.math.floor

class SegmentedProgressBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0,
) : View(context, attrs, defStyle) {

    constructor(context: Context, segments: Int) : this(context) {
        mode = Mode.COUNT
        this.segments = segments.coerceAtLeast(1)
    }

    enum class Mode { COUNT, FIXED_SIZE }

    private var mode = Mode.COUNT
    private var segments = 10
    private var minSegments = 1
    private var maxSegments = 100
    private var segmentWidth = dp(12f)
    private var gap = dp(6f)
    private var barHeight = dp(10f)
    private var corner = barHeight / 2f
    private var bgColor = 0xFF2F3234.toInt()
    private var fgColor = 0xFFE8DFCB.toInt()
    private var progress = 0f // 0..1

    private val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL }
    private val fgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL }
    private val rect = RectF()

    init {
        context.obtainStyledAttributes(attrs, R.styleable.SegmentedProgressBar).apply {
            mode = if (getInt(
                    R.styleable.SegmentedProgressBar_spb_mode, 0
                ) == 0
            ) Mode.COUNT else Mode.FIXED_SIZE
            segments =
                getInt(R.styleable.SegmentedProgressBar_spb_segments, segments).coerceAtLeast(1)
            segmentWidth =
                getDimension(R.styleable.SegmentedProgressBar_spb_segmentWidth, segmentWidth)
            minSegments =
                getInt(R.styleable.SegmentedProgressBar_spb_minSegments, minSegments).coerceAtLeast(
                    1
                )
            maxSegments =
                getInt(R.styleable.SegmentedProgressBar_spb_maxSegments, maxSegments).coerceAtLeast(
                    minSegments
                )
            gap = getDimension(R.styleable.SegmentedProgressBar_spb_gap, gap)
            barHeight = getDimension(R.styleable.SegmentedProgressBar_spb_height, barHeight)
            corner = getDimension(R.styleable.SegmentedProgressBar_spb_cornerRadius, corner)
            bgColor = getColor(R.styleable.SegmentedProgressBar_spb_bgColor, bgColor)
            fgColor = getColor(R.styleable.SegmentedProgressBar_spb_fgColor, fgColor)
            progress = getFloat(R.styleable.SegmentedProgressBar_spb_progress, progress)
            recycle()
        }
        bgPaint.color = bgColor
        fgPaint.color = fgColor
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val h = resolveSize(barHeight.toInt(), heightMeasureSpec)
        setMeasuredDimension(resolveSize(suggestedMinimumWidth, widthMeasureSpec), h)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (mode == Mode.FIXED_SIZE) recomputeSegmentsForFixedSize()
    }

    private fun recomputeSegmentsForFixedSize() {
        val avail = (width - paddingLeft - paddingRight).toFloat()
        if (avail <= 0f) return
        val pack = segmentWidth + gap
        val count = floor((avail + gap) / pack).toInt()
        segments = count.coerceIn(minSegments, maxSegments)
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        val availW = (width - paddingLeft - paddingRight).toFloat()
        if (availW <= 0f || segments <= 0) return

        val gapsTotal = gap * (segments - 1)
        val segW = if (mode == Mode.COUNT) {
            ((availW - gapsTotal) / segments)
        } else {
            segmentWidth
        }

        val totalBarW = segW * segments + gapsTotal
        var x = paddingLeft + max(0f, (availW - totalBarW) / 2f)
        val top = (height - barHeight) / 2f
        val bottom = top + barHeight

        repeat(segments) {
            rect.set(x, top, x + segW, bottom)
            canvas.drawRoundRect(rect, corner, corner, bgPaint)
            x += segW + gap
        }

        val progInSegs = progress.coerceIn(0f, 1f) * segments
        val full = floor(progInSegs).toInt()
        val part = progInSegs - full

        x = paddingLeft + max(0f, (availW - totalBarW) / 2f)
        repeat(segments) { i ->
            rect.set(x, top, x + segW, bottom)
            when {
                i < full -> canvas.drawRoundRect(rect, corner, corner, fgPaint)
                i == full && part > 0f -> {
                    rect.right = x + segW * part
                    canvas.drawRoundRect(rect, corner, corner, fgPaint)
                }
            }
            x += segW + gap
        }
    }

    fun setProgress(value: Float, animate: Boolean = false, duration: Long = 300) {
        val target = value.coerceIn(0f, 1f)
        if (!animate) {
            progress = target; invalidate(); return
        }
        val start = progress
        ValueAnimator.ofFloat(start, target).apply {
            this.duration = duration
            addUpdateListener { progress = it.animatedValue as Float; invalidate() }
        }.start()
    }

    fun setSegments(count: Int) {
        mode = Mode.COUNT
        segments = count.coerceAtLeast(1)
        invalidate()
    }

    fun setFixedSize(segmentWidthDp: Float, gapDp: Float? = null) {
        mode = Mode.FIXED_SIZE
        segmentWidth = dp(segmentWidthDp)
        gapDp?.let { gap = dp(it) }
        requestLayout(); invalidate()
    }

    private fun dp(v: Float) = v * resources.displayMetrics.density
}
