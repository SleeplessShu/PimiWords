package com.sleeplessdog.matchthewords.game.presentation

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.sleeplessdog.matchthewords.R
import com.sleeplessdog.matchthewords.databinding.GameFragmentBinding
import com.sleeplessdog.matchthewords.game.presentation.fragments.EndGameFragment
import com.sleeplessdog.matchthewords.game.presentation.fragments.LoadingFragment
import com.sleeplessdog.matchthewords.game.presentation.fragments.MatchSettingsFragment
import com.sleeplessdog.matchthewords.game.presentation.fragments.TrueOrFalseFragment
import com.sleeplessdog.matchthewords.game.presentation.fragments.WordsMatchingFragment
import com.sleeplessdog.matchthewords.game.presentation.models.GameState
import com.sleeplessdog.matchthewords.game.presentation.models.GameType
import com.sleeplessdog.matchthewords.game.presentation.models.MatchState
import org.koin.androidx.viewmodel.ext.android.viewModel

class GameFragment : Fragment() {

    private val args: GameFragmentArgs by navArgs()
    private val viewModel: GameViewModel by viewModel()
    private var _binding: GameFragmentBinding? = null
    private val binding: GameFragmentBinding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = GameFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val currentType = args.gameType
        viewModel.setGame(currentType)
        setupObservers()
    }


    private fun setupObservers() {

        viewModel.gameState.observe(viewLifecycleOwner) { newState ->
            Log.d("DEBUG", "setupObservers: ${newState.state} ")
            when (newState.state) {

                GameState.MATCH_SETTINGS -> {
                    childFragmentManager.beginTransaction()
                        .replace(R.id.flFragmentContainer, MatchSettingsFragment()).commit()
                    binding.tvHeader.setText(R.string.state_title_match_settings)
                    binding.statsBlock.isVisible = false
                }

                GameState.LOADING -> {
                    childFragmentManager.beginTransaction()
                        .replace(R.id.flFragmentContainer, LoadingFragment()).commit()
                    binding.tvHeader.setText(R.string.state_title_loading)
                    binding.statsBlock.isVisible = false
                }

                GameState.GAME -> {
                    launchGame()
                    binding.tvHeader.setText(R.string.empty)
                    binding.statsBlock.isVisible = true
                }

                GameState.END_OF_GAME -> {
                    childFragmentManager.beginTransaction()
                        .replace(R.id.flFragmentContainer, EndGameFragment()).commit()
                    binding.tvHeader.setText(R.string.empty)
                    binding.statsBlock.isVisible = false
                }
            }
        }
        viewModel.gameState.observe(viewLifecycleOwner) { gameState ->
            binding.tvScores.setText(gameState.score)
            setHearts(gameState.lives)
        }
    }

    private fun launchGame(){
        val gameType = viewModel.gameState.value?.gameType ?: GameType.MATCH8
        when (gameType) {
            GameType.MATCH8 -> {
                childFragmentManager.beginTransaction()
                    .replace(R.id.flFragmentContainer, WordsMatchingFragment()).commit()
            }

            GameType.TRUEorFALSE -> {
                childFragmentManager.beginTransaction()
                    .replace(R.id.flFragmentContainer, TrueOrFalseFragment()).commit()
            }
        }
    }

    private fun setHearts(heartsQuantity: Int) {
        when (heartsQuantity) {
            3 -> {
                binding.heart1.setImageResource(R.drawable.heart2)
                binding.heart2.setImageResource(R.drawable.heart2)
                binding.heart3.setImageResource(R.drawable.heart2)
            }

            2 -> {
                binding.heart1.setImageResource(R.drawable.heart2)
                binding.heart2.setImageResource(R.drawable.heart2)
                binding.heart3.setImageResource(R.drawable.ic_face)
            }

            1 -> {
                binding.heart1.setImageResource(R.drawable.heart2)
                binding.heart2.setImageResource(R.drawable.ic_face)
                binding.heart3.setImageResource(R.drawable.ic_face)
            }

            0 -> {
                binding.heart1.setImageResource(R.drawable.ic_face)
                binding.heart2.setImageResource(R.drawable.ic_face)
                binding.heart3.setImageResource(R.drawable.ic_face)
            }

        }
    }
}