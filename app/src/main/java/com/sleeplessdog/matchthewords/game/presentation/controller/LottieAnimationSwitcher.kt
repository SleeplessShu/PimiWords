package com.sleeplessdog.matchthewords.game.presentation.controller

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.annotation.RawRes
import androidx.core.view.isVisible
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable
import kotlin.math.max

class LottieAnimationSwitcher {

    /**
     * @param resFrom — анимация, которая проигрывается первой
     * @param resTo — анимация, на которую происходит переход
     * @param fromView — view для первой анимации
     * @param toView — view для второй анимации
     * @param cutFromEndFrames — за сколько кадров до конца from делать переключение
     * @param toStartFrame — безопасный стартовый кадр для to (боремся с артефактами)
     * @param loopFrom — зацикливать ли from
     * @param loopTo — зацикливать ли to
     */

    fun play(
        @RawRes resFrom: Int,
        @RawRes resTo: Int,
        fromView: LottieAnimationView,
        toView: LottieAnimationView,
        cutFromEndFrames: Int = 10,
        toStartFrame: Int = 4,
        loopTo: Boolean = false,
    ) {
        toView.apply {
            removeAllAnimatorListeners()
            animate().cancel()

            isVisible = false
            alpha = 1f

            setAnimation(resTo)
            repeatCount = if (loopTo) LottieDrawable.INFINITE else 0
            setFrame(toStartFrame)
        }

        fromView.apply {
            removeAllAnimatorListeners()
            animate().cancel()

            isVisible = true
            alpha = 1f

            setAnimation(resFrom)
            repeatCount = 0
            //repeatCount = if (loopFrom) LottieDrawable.INFINITE else 0
            setFrame(0)
        }

        var switched = false

        fromView.addAnimatorUpdateListener {
            if (switched) return@addAnimatorUpdateListener

            val maxFrame = fromView.maxFrame
            if (maxFrame <= 0f) return@addAnimatorUpdateListener

            val switchFrame = max(0f, maxFrame - cutFromEndFrames.toFloat())

            if (fromView.frame >= switchFrame) {
                switched = true

                fromView.pauseAnimation()
                fromView.isVisible = false

                toView.isVisible = true
                toView.playAnimation()
            }
        }

        fromView.addAnimatorListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                fromView.removeAllAnimatorListeners()
            }

            override fun onAnimationCancel(animation: Animator) {
                fromView.removeAllAnimatorListeners()
            }
        })

        fromView.playAnimation()
    }

    fun playLottieToImage(
        lottieView: LottieAnimationView,
        imageView: ImageView,
        @DrawableRes imageRes: Int,
        showImageBeforeEndFrames: Int = 10,
        cutFromEndFrames: Int = 0,
        lottieStartFrame: Int = 0,
        hideLottieAtEnd: Boolean = true,
        onFinished: (() -> Unit)? = null,
    ) {
        imageView.apply {
            isVisible = false
            setImageResource(imageRes)
            alpha = 1f
        }

        lottieView.apply {
            removeAllAnimatorListeners()
            removeAllUpdateListeners()
            animate().cancel()

            isVisible = true
            alpha = 1f
            repeatCount = 0
            setFrame(lottieStartFrame)
        }

        var imageShown = false
        var finished = false

        lottieView.addAnimatorUpdateListener {
            if (finished) return@addAnimatorUpdateListener

            val maxFrame = lottieView.maxFrame
            if (maxFrame <= 0f) return@addAnimatorUpdateListener

            val showImageFrame = max(0f, maxFrame - showImageBeforeEndFrames.toFloat())
            val cutFrame = max(0f, maxFrame - cutFromEndFrames.toFloat())

            if (!imageShown && lottieView.frame >= showImageFrame) {
                imageShown = true
                imageView.isVisible = true
            }

            // 2) обрезать хвост и завершить
            if (lottieView.frame >= cutFrame) {
                finished = true

                lottieView.pauseAnimation()
                if (hideLottieAtEnd) lottieView.isVisible = false

                // подчистить слушатели
                lottieView.removeAllUpdateListeners()
                lottieView.removeAllAnimatorListeners()

                onFinished?.invoke()
            }
        }

        lottieView.addAnimatorListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                if (!finished) {
                    finished = true
                    imageView.isVisible = true
                    if (hideLottieAtEnd) lottieView.isVisible = false
                    onFinished?.invoke()
                }
                lottieView.removeAllUpdateListeners()
                lottieView.removeAllAnimatorListeners()
            }

            override fun onAnimationCancel(animation: Animator) {
                lottieView.removeAllUpdateListeners()
                lottieView.removeAllAnimatorListeners()
            }
        })

        lottieView.playAnimation()
    }

    private fun LottieAnimationView.removeAllUpdateListeners() {
        try {
            val method = this::class.java.getMethod("removeAllUpdateListeners")
            method.invoke(this)
        } catch (_: Throwable) {

        }
    }
}
