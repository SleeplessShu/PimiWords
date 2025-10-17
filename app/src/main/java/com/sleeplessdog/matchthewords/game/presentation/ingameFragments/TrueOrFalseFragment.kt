package com.sleeplessdog.matchthewords.game.presentation.ingameFragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.sleeplessdog.matchthewords.R
import com.sleeplessdog.matchthewords.databinding.GameTrueOrFalseBinding
import com.sleeplessdog.matchthewords.game.presentation.GameViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class TrueOrFalseFragment : Fragment(R.layout.game_true_or_false) {
    private val parentVM: GameViewModel by sharedViewModel(
        owner = { requireParentFragment() })
    private val childVM: TrueOrFalseViewModel by viewModel()

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

    private fun setupObservers() {
        parentVM.wordsPairs.observe(viewLifecycleOwner) { pool ->
            if (pool.isNullOrEmpty()) return@observe
            childVM.setPool(pool)
            binding.root.isVisible = true
        }

        childVM.ui.observe(viewLifecycleOwner) { ui ->
            if (ui == null) return@observe
            binding.tWord.text = ui.word.text
            binding.tTranslate.text = ui.shownTranslation.text
            binding.bTrue.isEnabled = !ui.locked
            binding.bFalse.isEnabled = !ui.locked
        }

        childVM.events.observe(viewLifecycleOwner) { e ->
            parentVM.onGameEvent(e)
        }

        binding.bTrue.setOnClickListener { childVM.onTrueClicked() }
        binding.bFalse.setOnClickListener { childVM.onFalseClicked() }
    }
}
