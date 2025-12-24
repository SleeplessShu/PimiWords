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
import com.sleeplessdog.matchthewords.game.presentation.models.EndGameStats
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class EndGameFragment : Fragment(R.layout.end_game_fragment) {
    private val parentViewModel: GameViewModel by sharedViewModel(owner = { requireParentFragment() })
    private var _binding: EndGameFragmentBinding? = null
    private val binding: EndGameFragmentBinding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
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
            returnToGameSelect()
        }
        binding.bRestart.setOnClickListener {
            parentViewModel.restartGame()
        }
        binding.bToSettings.setOnClickListener {
            goToSettings()
        }
    }

    private fun setupObservers() {
        parentViewModel.endGameStats.observe(viewLifecycleOwner) { stats ->

            val isWin = stats.isWin

            if (isWin) {
                showResult(
                    result = getString(R.string.end_game_phrase_win),
                    phrase = getString(R.string.eg_congrats),
                    animation = R.raw.animation_victory_default,
                    stats = stats
                )
            } else {
                showResult(
                    result = getString(R.string.end_game_phrase_loose),
                    phrase = getString(R.string.eg_sorrow),
                    animation = R.drawable.pimi_game_end_fail,
                    stats = stats,
                )
            }
        }
    }

    private fun showResult(result: String, phrase: String, animation: Int, stats: EndGameStats?) {
        val errors = stats?.mistakesCount
        val score = stats?.score
        val words = stats?.wordsCount

        binding.tvResult.text = result
        binding.tvPhrase.text = phrase
        binding.tvErrors.text = errors.toString()
        binding.tvScore.text = score.toString()
        binding.tvWords.text = words.toString()
        binding.animationView.setAnimation(animation)
        binding.animationView.playAnimation()
    }

    private fun returnToGameSelect() {
        val dir = GameFragmentDirections
            .actionGameFragmentToGameSelectFragment()
        findNavController().navigate(dir)
    }

    private fun goToSettings() {
        val dir = GameFragmentDirections
            .actionGameFragmentToSettingsFragment()
        findNavController().navigate(dir)
    }
}



