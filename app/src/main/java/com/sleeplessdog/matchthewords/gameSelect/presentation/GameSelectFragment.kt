package com.sleeplessdog.matchthewords.gameSelect.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.sleeplessdog.matchthewords.databinding.GameSelectFragmentBinding
import com.sleeplessdog.matchthewords.game.presentation.models.GameType
import org.koin.androidx.viewmodel.ext.android.viewModel

class GameSelectFragment : Fragment() {
    private val viewModel: GameSelectViewModel by viewModel()
    private var _binding: GameSelectFragmentBinding? = null
    private val binding: GameSelectFragmentBinding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = GameSelectFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()

    }

    private fun setupObservers() {
        binding.match8.setOnClickListener {
            goToGameSettings(GameType.MATCH8)
        }
        binding.trueOrFalse.setOnClickListener {
            goToGameSettings(GameType.TRUEorFALSE)
        }
        binding.b3.setOnClickListener { showToast() }
        binding.b4.setOnClickListener { showToast() }
        binding.b5.setOnClickListener { showToast() }
    }

    private fun showToast() {
        Toast.makeText(requireContext(), "game under construction", Toast.LENGTH_SHORT)
            .show()
    }

    private fun goToGameSettings(gameType: GameType) {
        val dir = GameSelectFragmentDirections
            .actionGameSelectFragmentToGameFragment(gameType)
        findNavController().navigate(dir)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}