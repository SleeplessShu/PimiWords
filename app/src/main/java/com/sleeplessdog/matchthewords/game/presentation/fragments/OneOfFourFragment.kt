package com.sleeplessdog.matchthewords.game.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import com.sleeplessdog.matchthewords.R
import com.sleeplessdog.matchthewords.databinding.GameOneOfFourBinding
import com.sleeplessdog.matchthewords.game.presentation.GameViewModel
import com.sleeplessdog.matchthewords.game.presentation.OneOfFourViewModel
import com.sleeplessdog.matchthewords.game.presentation.models.AnswerEvent
import com.sleeplessdog.matchthewords.game.presentation.models.ButtonState
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

class OneOfFourFragment : Fragment(R.layout.game_one_of_four) {

    private val parentViewModel: GameViewModel by sharedViewModel( owner = { requireParentFragment() })
    private val oneOfFourViewModel: OneOfFourViewModel by viewModel()

    private var _binding: GameOneOfFourBinding? = null
    private val binding get() = _binding!!
    inline fun <T> LiveData<T>.observeOnce(
        owner: LifecycleOwner,
        crossinline block: (T) -> Unit
    ) {
        observe(owner) { t ->
            block(t)
            removeObservers(owner)
        }
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = GameOneOfFourBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        parentViewModel.wordsPairs.observeOnce(viewLifecycleOwner) { pairs ->
            oneOfFourViewModel.setPool(pairs)
            binding.root.isVisible = true
        }

        oneOfFourViewModel.ui.observe(viewLifecycleOwner) { ui ->
            binding.original.text = ui.originalText
            val buttons = listOf(binding.b1, binding.b2, binding.b3, binding.b4)
            buttons.forEachIndexed { i, b ->
                b.text = ui.options.getOrNull(i) ?: ""
                b.isEnabled = ui.states.getOrNull(i) != ButtonState.DISABLED
                b.background = requireContext().getDrawable(getBackgroundRes(ui.states.getOrNull(i)))
            }
        }

        oneOfFourViewModel.answerEvents.observe(viewLifecycleOwner) { ev ->
            when (ev) {
                AnswerEvent.CORRECT -> parentViewModel.reactOnCorrect()
                AnswerEvent.WRONG -> parentViewModel.reactOnError()
            }
        }

        // Навигация по завершению — просим родителя завершить игру
        oneOfFourViewModel.completed.observe(viewLifecycleOwner) { done ->
            if (done == true) {
                parentViewModel.onGameEnd()
            }
        }

        // Клики
        binding.b1.setOnClickListener { oneOfFourViewModel.onAnswerClick(0) }
        binding.b2.setOnClickListener { oneOfFourViewModel.onAnswerClick(1) }
        binding.b3.setOnClickListener { oneOfFourViewModel.onAnswerClick(2) }
        binding.b4.setOnClickListener { oneOfFourViewModel.onAnswerClick(3) }
    }

    private fun getBackgroundRes(state: ButtonState?): Int = when (state) {
        ButtonState.ERROR   -> R.drawable.word_background_error
        ButtonState.CORRECT -> R.drawable.word_background_correct
        else                                -> R.drawable.word_background_default
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}