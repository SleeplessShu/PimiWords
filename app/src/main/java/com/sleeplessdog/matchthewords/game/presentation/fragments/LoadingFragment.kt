package com.sleeplessdog.matchthewords.game.presentation.fragments

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.sleeplessdog.matchthewords.R
import com.sleeplessdog.matchthewords.databinding.LoadingFragmentBinding
import com.sleeplessdog.matchthewords.utils.ConstantsApp

class LoadingFragment : Fragment(R.layout.loading_fragment) {
    private var binding: LoadingFragmentBinding? = null

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = LoadingFragmentBinding.bind(view)
        startLoadingAnimation()
    }

    private fun startLoadingAnimation() {
        val b = requireNotNull(binding)
        val scaleFactor = ConstantsApp.LOADING_SCALE_FACTOR

        b.starMain.pulse(
            scale = scaleFactor,
            durationMs = ConstantsApp.LOADING_PULSE_MAIN_DURATION,
            startDelayMs = ConstantsApp.LOADING_PULSE_MAIN_DELAY
        )

        b.starTr.pulse(
            scale = scaleFactor,
            durationMs = ConstantsApp.LOADING_PULSE_TR_DURATION,
            startDelayMs = ConstantsApp.LOADING_PULSE_TR_DELAY
        )

        b.starBl.pulse(
            scale = scaleFactor,
            durationMs = ConstantsApp.LOADING_PULSE_BL_DURATION,
            startDelayMs = ConstantsApp.LOADING_PULSE_BL_DELAY
        )

        b.starBr.pulse(
            scale = scaleFactor,
            durationMs = ConstantsApp.LOADING_PULSE_BR_DURATION,
            startDelayMs = ConstantsApp.LOADING_PULSE_BR_DELAY
        )

        b.starTl.pulse(
            scale = scaleFactor,
            durationMs = ConstantsApp.LOADING_PULSE_TL_DURATION,
            startDelayMs = ConstantsApp.LOADING_PULSE_TL_DELAY
        )
    }

    private fun View.pulse(
        scale: Float,
        durationMs: Long,
        startDelayMs: Long = ConstantsApp.ZERO_DURATION_MS,
    ) {
        val scaleX = ObjectAnimator.ofFloat(
            this, View.SCALE_X, ConstantsApp.FULL_SCALE, scale, ConstantsApp.FULL_SCALE
        ).apply {
            duration = durationMs
            startDelay = startDelayMs
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.REVERSE
        }

        val scaleY = ObjectAnimator.ofFloat(
            this, View.SCALE_Y, ConstantsApp.FULL_SCALE, scale, ConstantsApp.FULL_SCALE
        ).apply {
            duration = durationMs
            startDelay = startDelayMs
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.REVERSE
        }

        AnimatorSet().apply {
            playTogether(scaleX, scaleY)
            start()
        }
    }
}
