package com.sleeplessdog.matchthewords.game.presentation.ingameFragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.sleeplessdog.matchthewords.R
import com.sleeplessdog.matchthewords.databinding.WordsMatchingFragmentBinding
import com.sleeplessdog.matchthewords.game.presentation.GameViewModel
import com.sleeplessdog.matchthewords.game.presentation.holders.WordsMatchingAdapter
import com.sleeplessdog.matchthewords.game.presentation.models.Word
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class WordsMatchingFragment : Fragment(R.layout.words_matching_fragment) {
    private val parentVM: GameViewModel by activityViewModel()
    private val childVM: WordsMatchingViewModel by viewModel()

    private var _binding: WordsMatchingFragmentBinding? = null
    private val binding: WordsMatchingFragmentBinding get() = _binding!!

    private lateinit var adapter: WordsMatchingAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = WordsMatchingFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
        setupUI()
    }

    private fun setupUI() {
        adapter = WordsMatchingAdapter(
            onWordClick = { word: Word -> childVM.onWordClick(word) })
        binding.rvWordsList.layoutManager = LinearLayoutManager(requireContext())
        binding.rvWordsList.adapter = adapter
    }

    private fun setupObservers() {
        parentVM.wordsPairs.observe(viewLifecycleOwner) { allPairs ->
            if (!allPairs.isNullOrEmpty()) {
                childVM.setPool(allPairs)
                binding.root.isVisible = true
            }
        }

        childVM.pagePairs.observe(viewLifecycleOwner) { page ->
            adapter.submitList(page ?: emptyList())
        }

        childVM.state.observe(viewLifecycleOwner) { state ->
            adapter.selectedWords = state.selectedWords
            adapter.errorWords = state.errorWords
            adapter.correctWords = state.correctWords.map { it.id }.toSet()
            adapter.usedWords = state.usedWords.map { it.id }.toSet()
        }

        childVM.events.observe(viewLifecycleOwner) { e ->
            parentVM.onGameEvent(e)
        }
    }
}
