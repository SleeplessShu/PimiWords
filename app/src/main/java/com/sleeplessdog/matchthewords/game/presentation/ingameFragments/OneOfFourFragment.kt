package com.sleeplessdog.matchthewords.game.presentation.ingameFragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.sleeplessdog.matchthewords.R
import com.sleeplessdog.matchthewords.databinding.GameOneOfFourBinding
import com.sleeplessdog.matchthewords.game.presentation.GameViewModel
import com.sleeplessdog.matchthewords.game.presentation.models.ButtonState
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

class OneOfFourFragment : Fragment(R.layout.game_one_of_four) {

    private val parentVM: GameViewModel by sharedViewModel(owner = { requireParentFragment() })
    private val childVM: OneOfFourViewModel by viewModel()

    private var _binding: GameOneOfFourBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
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

        // Рендер UI вопроса
        childVM.ui.observe(viewLifecycleOwner) { ui ->
            binding.original.text = ui.originalText
            val buttons = listOf(binding.b1, binding.b2, binding.b3, binding.b4)
            buttons.forEachIndexed { i, b ->
                b.text = ui.options.getOrNull(i) ?: ""
                b.isEnabled = !ui.locked && ui.states.getOrNull(i) != ButtonState.DISABLED
                b.background = requireContext().getDrawable(getBackgroundRes(ui.states.getOrNull(i)))
            }
        }

        childVM.events.observe(viewLifecycleOwner) { e ->
            parentVM.onGameEvent(e)
        }

        // Клики
        binding.b1.setOnClickListener { childVM.onAnswerClick(0) }
        binding.b2.setOnClickListener { childVM.onAnswerClick(1) }
        binding.b3.setOnClickListener { childVM.onAnswerClick(2) }
        binding.b4.setOnClickListener { childVM.onAnswerClick(3) }
    }

    private fun getBackgroundRes(state: ButtonState?): Int = when (state) {
        ButtonState.ERROR   -> R.drawable.word_background_error
        ButtonState.CORRECT -> R.drawable.word_background_correct
        ButtonState.DISABLED -> R.drawable.word_background_used
        else -> R.drawable.word_background_default
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}