package com.sleeplessdog.matchthewords.game.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.sleeplessdog.matchthewords.R
import com.sleeplessdog.matchthewords.databinding.EndGameFragmentBinding
import com.sleeplessdog.matchthewords.game.presentation.GameFragmentDirections
import com.sleeplessdog.matchthewords.game.presentation.GameViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class EndGameFragment : Fragment(R.layout.end_game_fragment) {
    private val parentViewModel: GameViewModel by sharedViewModel(owner = { requireParentFragment() })
    private var _binding: EndGameFragmentBinding? = null
    private val binding: EndGameFragmentBinding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = EndGameFragmentBinding.inflate(inflater, container, false)
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


        binding.bNewGame.setOnClickListener {
            parentViewModel.onGame()
        }
        binding.bRestart.setOnClickListener {
            parentViewModel.restartGame()
        }
        binding.bToMenu.setOnClickListener {
           returnToGameSelect()
        }
    }

    private fun setupObservers() {
        parentViewModel.statsState.observe(viewLifecycleOwner) { state ->
            if (state.lives == 0) {
                binding.tvResult.setText(R.string.end_game_phrase_loose)
                binding.pimi.setImageResource(R.drawable.pimi_game_end_fail)
            } else {
                binding.tvResult.setText(R.string.end_game_phrase_win)
                binding.pimi.setImageResource(R.drawable.pimi_game_end_victory)
            }
            val score = state.score.toIntOrNull() ?: 0
            val scoreText = getString(R.string.score_text, score)
            binding.tvAnnouncement.text = StringBuilder(scoreText)
        }
    }

    private fun returnToGameSelect() {
        val dir = GameFragmentDirections
            .actionGameFragmentToGameSelectFragment()
        findNavController().navigate(dir)
    }
}



