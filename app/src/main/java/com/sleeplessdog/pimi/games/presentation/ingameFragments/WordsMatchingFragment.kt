package com.sleeplessdog.pimi.games.presentation.ingameFragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.sleeplessdog.pimi.R
import com.sleeplessdog.pimi.databinding.WordsMatchingFragmentBinding
import com.sleeplessdog.pimi.games.presentation.GameViewModel
import com.sleeplessdog.pimi.games.presentation.holders.WordsMatchingAdapter
import com.sleeplessdog.pimi.games.presentation.models.Word
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class WordsMatchingFragment : Fragment(R.layout.words_matching_fragment) {
    private val parentVM: GameViewModel by sharedViewModel(
        owner = { requireParentFragment() })
    private val childVM: WordsMatchingViewModel by viewModel()


    private var _binding: WordsMatchingFragmentBinding? = null
    private val binding: WordsMatchingFragmentBinding get() = _binding!!

    private lateinit var adapter: WordsMatchingAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
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
            adapter.updateWordsList(page ?: emptyList())
        }

        childVM.state.observe(viewLifecycleOwner) { state ->
            adapter.updateSelectedWords(state.selectedWords)
            adapter.updateErrorWords(state.errorWords)
            adapter.updateCorrectWords(state.correctWords)
            adapter.updateUsedWords(state.usedWords)
        }

        childVM.events.observe(viewLifecycleOwner) { e ->
            parentVM.onGameEvent(e)
        }
    }
}