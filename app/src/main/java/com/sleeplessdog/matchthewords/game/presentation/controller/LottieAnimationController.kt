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
import kotlin.math.roundToInt

class LottieAnimationController {

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


    fun switchBetweenTwo(
        @RawRes resFrom: Int,
        @RawRes resTo: Int,
        fromView: LottieAnimationView,
        toView: LottieAnimationView,
        cutFromEndFrames: Int = 10,
        toStartFrame: Int = 4,
        loopTo: Boolean = false,
        onFinished: (() -> Unit)? = null,
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

        // Подготовка FROM
        fromView.apply {
            removeAllAnimatorListeners()
            animate().cancel()

            isVisible = true
            alpha = 1f

            setAnimation(resFrom)
            repeatCount = 0
            setFrame(0)
        }

        var switched = false
        var finishedCalled = false

        fun callFinishedOnce() {
            if (finishedCalled) return
            finishedCalled = true
            onFinished?.invoke()
        }

        // слушатель завершения TO (нам нужен именно он)
        toView.addAnimatorListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                // если loopTo=true — сюда обычно не попадём
                toView.removeAllAnimatorListeners()
                callFinishedOnce()
            }

            override fun onAnimationCancel(animation: Animator) {
                toView.removeAllAnimatorListeners()
            }
        })

        // момент переключения FROM -> TO (обрезка хвоста)
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

                // на всякий случай: если toView стоит на нулевой длительности/кадре и сразу "закончилась"
                // то onAnimationEnd может не прилететь. Тогда вызовем сразу, но только если не loop.
                if (!loopTo && toView.maxFrame <= toView.frame) {
                    callFinishedOnce()
                }
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

    fun playLoopCut(
        @RawRes res: Int,
        view: LottieAnimationView,
        loop: Boolean = true,
        cutFromStartFrames: Int = 0,
        cutFromEndFrames: Int = 0,
    ) {
        view.removeAllAnimatorListeners()
        view.removeAllLottieOnCompositionLoadedListener()

        view.setAnimation(res)

        view.addLottieOnCompositionLoadedListener { composition ->
            val maxFrame = composition.endFrame
            val startFrame =
                (composition.startFrame + cutFromStartFrames).coerceAtLeast(composition.startFrame)
            val endFrame = (maxFrame - cutFromEndFrames).coerceAtMost(maxFrame)

            // защита от кривых значений
            val safeStart = startFrame.coerceAtMost(endFrame)
            val safeEnd = endFrame.coerceAtLeast(safeStart)

            view.repeatCount = if (loop) LottieDrawable.INFINITE else 0

            // важно: ограничиваем диапазон
            view.setMinAndMaxFrame(
                safeStart.roundToInt(),
                safeEnd.roundToInt()
            )

            // каждый запуск будет начинаться с minFrame
            view.setFrame(safeStart.roundToInt())
            view.playAnimation()
        }

        // если композиция уже загружена — listener может не вызваться,
        // поэтому вручную дернем конфиг на уже загруженной:
        view.composition?.let { composition ->
            val maxFrame = composition.endFrame
            val startFrame =
                (composition.startFrame + cutFromStartFrames).coerceAtLeast(composition.startFrame)
            val endFrame = (maxFrame - cutFromEndFrames).coerceAtMost(maxFrame)

            val safeStart = startFrame.coerceAtMost(endFrame)
            val safeEnd = endFrame.coerceAtLeast(safeStart)

            view.repeatCount = if (loop) LottieDrawable.INFINITE else 0
            view.setMinAndMaxFrame(safeStart.roundToInt(), safeEnd.roundToInt())
            view.setFrame(safeStart.roundToInt())
            view.playAnimation()
        }
    }

    fun playUnderOnce(
        loopView: LottieAnimationView,          // верхний, который сейчас зациклен
        underView: LottieAnimationView,         // нижний (под ним), где проиграем lottie2
        @RawRes lottie2: Int,
        lottie2StartFrame: Int = 0,
        onFinished: (() -> Unit)? = null,
    ) {
        // готовим underView
        underView.apply {
            removeAllAnimatorListeners()
            removeAllLottieOnCompositionLoadedListener()
            animate().cancel()

            isVisible = true
            alpha = 1f

            setAnimation(lottie2)
            repeatCount = 0
        }

        fun startUnderAndHideLoop() {
            // 1️⃣ запускаем нижний
            underView.setFrame(lottie2StartFrame)
            underView.playAnimation()

            // 2️⃣ В ЭТОТ ЖЕ КАДР прячем верхний
            loopView.apply {
                removeAllAnimatorListeners()
                removeAllLottieOnCompositionLoadedListener()
                animate().cancel()
                pauseAnimation()
                isVisible = false
            }
        }

        // если композиция уже загружена
        if (underView.composition != null) {
            startUnderAndHideLoop()
        } else {
            underView.addLottieOnCompositionLoadedListener {
                startUnderAndHideLoop()
            }
        }

        underView.addAnimatorListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                underView.removeAllAnimatorListeners()
                onFinished?.invoke()
            }

            override fun onAnimationCancel(animation: Animator) {
                underView.removeAllAnimatorListeners()
            }
        })
    }


    private fun LottieAnimationView.removeAllUpdateListeners() {
        try {
            val method = this::class.java.getMethod("removeAllUpdateListeners")
            method.invoke(this)
        } catch (_: Throwable) {

        }
    }
}
