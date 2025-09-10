package com.sleeplessdog.matchthewords.game.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.sleeplessdog.matchthewords.game.presentation.GameViewModel
import com.sleeplessdog.matchthewords.R
import com.sleeplessdog.matchthewords.databinding.GameTrueOrFalseBinding
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import kotlin.getValue

class TrueOrFalseFragment : Fragment(R.layout.game_true_or_false) {
        private val parentViewModel: GameViewModel by sharedViewModel(
            owner = { requireParentFragment() }
        )
        private var _binding: GameTrueOrFalseBinding? = null
        private val binding: GameTrueOrFalseBinding get() = _binding!!

        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
        ): View? {
            _binding = GameTrueOrFalseBinding.inflate(inflater, container, false)
            return binding.root
        }

        override fun onDestroyView() {
            super.onDestroyView()
            _binding = null
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
            setupObservers()
        }

    private fun setupObservers(){
        parentViewModel.tfQuestion.observe(viewLifecycleOwner) { q ->
            if (q == null) return@observe
            binding.tWord.text = q.word.text
            binding.tTranslate.text = q.translation.text
        }
        binding.bTrue.setOnClickListener { parentViewModel.onTrueClicked() }
        binding.bFalse.setOnClickListener { parentViewModel.onFalseClicked() }
    }
}