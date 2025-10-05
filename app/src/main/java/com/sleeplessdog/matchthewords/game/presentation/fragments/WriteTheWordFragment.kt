package com.sleeplessdog.matchthewords.game.presentation.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.sleeplessdog.matchthewords.App
import com.sleeplessdog.matchthewords.R
import com.sleeplessdog.matchthewords.databinding.WriteTheWordFragmentBinding
import com.sleeplessdog.matchthewords.game.presentation.GameViewModel
import com.sleeplessdog.matchthewords.game.presentation.WriteTheWordViewModel
import com.sleeplessdog.matchthewords.game.presentation.holders.LettersAdapter
import com.sleeplessdog.matchthewords.game.presentation.models.AnswerEvent
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

class WriteTheWordFragment : Fragment(R.layout.write_the_word_fragment) {
    private val parentViewModel: GameViewModel by sharedViewModel(
        owner = { requireParentFragment() }
    )
    private val vm: WriteTheWordViewModel by viewModel()
    private var _binding: WriteTheWordFragmentBinding? = null
    private val binding: WriteTheWordFragmentBinding get() = _binding!!
    private lateinit var adapter: LettersAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = WriteTheWordFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        adapter = LettersAdapter(
            onClick = {pos -> vm.onLetterClick(pos)},
            context = App.appContext,)
        binding.rvLetters.adapter = adapter

        val flex = com.google.android.flexbox.FlexboxLayoutManager(requireContext()).apply {
            flexDirection = com.google.android.flexbox.FlexDirection.ROW
            flexWrap = com.google.android.flexbox.FlexWrap.WRAP
            justifyContent = com.google.android.flexbox.JustifyContent.CENTER
        }
        binding.rvLetters.layoutManager = flex

        vm.ui.observe(viewLifecycleOwner) { ui ->
            binding.tvPrompt.text = ui.prompt
            binding.tvInput.text = ui.input
            adapter.submitList(ui.letters)
            // можно подсветку/блокировку по ui.locked
        }

        vm.events.observe(viewLifecycleOwner) { ev ->
            when (ev) {
                AnswerEvent.CORRECT -> {
                    // зелёная подсветка + задержка и next round
                    binding.tvInput.setTextColor(requireContext().getColor(R.color.item_correct))
                    binding.tvInput.postDelayed({
                        binding.tvInput.setTextColor(requireContext().getColor(android.R.color.black))
                        // тут попросить родителя дать новую пару или самостоятельно:
                        // vm.startRound(newPrompt, newTranslation)
                        vm.next()
                    }, 800)
                }
                AnswerEvent.WRONG -> {
                    // красная подсветка/вибра
                    binding.tvInput.setTextColor(requireContext().getColor(R.color.item_wrong))
                    binding.tvInput.postDelayed({
                        binding.tvInput.setTextColor(requireContext().getColor(android.R.color.black))
                    }, 500)
                }
            }
        }

        binding.btnBackspace.setOnClickListener { vm.onBackspace() }
        binding.btnClear.setOnClickListener { vm.onClear() }
        binding.btnCheck.setOnClickListener { vm.onCheck() }

        // старт раунда (получаешь из родителя пару слово-перевод):
        // пример:
        vm.startRound(prompt = "retire", translation = "уходить в отставку")
    }

    override fun onDestroyView() {
        _binding = null;
        super.onDestroyView() }
}