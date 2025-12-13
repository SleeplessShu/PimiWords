package com.sleeplessdog.matchthewords.game.presentation.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.sleeplessdog.matchthewords.R
import com.sleeplessdog.matchthewords.databinding.EndGameFragmentBinding
import com.sleeplessdog.matchthewords.game.presentation.GameFragmentDirections
import com.sleeplessdog.matchthewords.game.presentation.GameViewModel
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class EndGameFragment : Fragment(R.layout.end_game_fragment) {
    private val parentViewModel: GameViewModel by activityViewModel()

    private var binding: EndGameFragmentBinding? = null

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = EndGameFragmentBinding.bind(view)
        setupObservers()
        setupUI()

    }

    private fun setupUI() {
        val b = requireNotNull(binding)
        b.bNewGame.setOnClickListener {
            parentViewModel.onGame()
        }
        b.bRestart.setOnClickListener {
            parentViewModel.restartGame()
        }
        b.bToMenu.setOnClickListener {
            returnToGameSelect()
        }
    }

    private fun setupObservers() {
        parentViewModel.statsState.observe(viewLifecycleOwner) { state ->
            val b = requireNotNull(binding)
            if (state.lives == 0) {
                b.tvResult.setText(R.string.end_game_phrase_loose)
                b.pimi.setImageResource(R.drawable.pimi_game_end_fail)
            } else {
                b.tvResult.setText(R.string.end_game_phrase_win)
                b.pimi.setImageResource(R.drawable.pimi_game_end_victory)
            }
            val score = state.score.toIntOrNull() ?: 0
            val scoreText = getString(R.string.score_text, score)
            b.tvAnnouncement.text = StringBuilder(scoreText)
        }
    }

    private fun returnToGameSelect() {
        val dir = GameFragmentDirections.actionGameFragmentToGameSelectFragment()
        findNavController().navigate(dir)
    }
}



