package com.sleeplessdog.matchthewords.game.presentation.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.sleeplessdog.matchthewords.R
import com.sleeplessdog.matchthewords.databinding.LoadingFragmentBinding

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
        b.animLoading.apply {
            setAnimation(R.raw.animations_loading)
            repeatCount = 0
            playAnimation()
        }
    }
}
