package com.sleeplessdog.matchthewords.game.presentation.fragments

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.sleeplessdog.matchthewords.R
import com.sleeplessdog.matchthewords.databinding.LoadingBinding

class LoadingFragment : Fragment(R.layout.loading) {

    private var _binding: LoadingBinding? = null
    private val binding: LoadingBinding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = LoadingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        startLoadingAnimation()
    }

    private fun startLoadingAnimation() {
        val scaleFactor = 1.3f // +30%
        binding.starMain.pulse(scaleFactor, 3000L, 80L)
        binding.starTr.pulse(scaleFactor, 2200L, 300L)
        binding.starBl.pulse(scaleFactor, 1400L, 200L)
        binding.starBr.pulse(scaleFactor, 1800L, 500L)
        binding.starTl.pulse(scaleFactor, 2000L, 100L)
    }

    fun View.pulse(scale: Float, duration: Long, startDelay: Long = 0L) {
        val scaleX = ObjectAnimator.ofFloat(this, View.SCALE_X, 1f, scale, 1f).apply {
            this.duration = duration
            this.startDelay = startDelay
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.REVERSE
        }

        val scaleY = ObjectAnimator.ofFloat(this, View.SCALE_Y, 1f, scale, 1f).apply {
            this.duration = duration
            this.startDelay = startDelay
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.REVERSE
        }

        AnimatorSet().apply {
            playTogether(scaleX, scaleY)
            start()
        }
    }
}