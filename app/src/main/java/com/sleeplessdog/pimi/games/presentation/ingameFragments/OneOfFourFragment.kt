package com.sleeplessdog.pimi.games.presentation.ingameFragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.sleeplessdog.pimi.R
import com.sleeplessdog.pimi.databinding.GameOneOfFourBinding
import com.sleeplessdog.pimi.games.presentation.GameViewModel
import com.sleeplessdog.pimi.games.presentation.models.ButtonState
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class OneOfFourFragment : Fragment(R.layout.game_one_of_four) {

    private val parentVM: GameViewModel by sharedViewModel(owner = { requireParentFragment() })
    private val childVM: OneOfFourViewModel by viewModel()

    private var _binding: GameOneOfFourBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View {
        _binding = GameOneOfFourBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        parentVM.wordsPairs.observe(viewLifecycleOwner) { pairs ->
            if (!pairs.isNullOrEmpty()) {
                childVM.setPool(pairs)
                binding.root.isVisible = true
            }
        }

        childVM.ui.observe(viewLifecycleOwner) { ui ->
            binding.original.text = ui.originalText
            val buttons = listOf(binding.b1, binding.b2, binding.b3, binding.b4)
            buttons.forEachIndexed { i, b ->
                val state = ui.states.getOrNull(i) ?: ButtonState.DEFAULT
                b.text = ui.options.getOrNull(i) ?: ""
                b.isEnabled = !ui.locked && state.enabled
                b.setBackgroundResource(getBackgroundRes(state))
                val color = androidx.core.content.ContextCompat.getColor(
                    requireContext(), state.textColorRes
                )
                b.setTextColor(color)
            }
        }

        childVM.events.observe(viewLifecycleOwner) { e ->
            parentVM.onGameEvent(e)
        }

        binding.b1.setOnClickListener { childVM.onAnswerClick(0) }
        binding.b2.setOnClickListener { childVM.onAnswerClick(1) }
        binding.b3.setOnClickListener { childVM.onAnswerClick(2) }
        binding.b4.setOnClickListener { childVM.onAnswerClick(3) }
    }

    private fun getBackgroundRes(state: ButtonState?) =
        state?.backgroundRes ?: R.drawable.bg_card_default


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}