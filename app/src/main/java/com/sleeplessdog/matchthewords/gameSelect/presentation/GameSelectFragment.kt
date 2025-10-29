package com.sleeplessdog.matchthewords.gameSelect.presentation

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.sleeplessdog.matchthewords.R
import com.sleeplessdog.matchthewords.databinding.GameSelectV2FragmentBinding
import com.sleeplessdog.matchthewords.game.presentation.models.GameType
import com.sleeplessdog.matchthewords.gameSelect.controller.GameOptionViewLong
import com.sleeplessdog.matchthewords.gameSelect.controller.GameOptionViewShort
import org.koin.androidx.viewmodel.ext.android.viewModel

class GameSelectFragment : Fragment() {
    private val viewModel: GameSelectViewModel by viewModel()
    private var _binding: GameSelectV2FragmentBinding? = null
    private val binding: GameSelectV2FragmentBinding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = GameSelectV2FragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupButtonsInfill()
        setupObservers()

    }

    private fun setupButtonsInfill() {
        binding.match6.setup(
            title = getString(R.string.MTW),
            iconNormalRes = R.drawable.game_mtw_icon_normal,
            iconSelectedRes = R.drawable.game_mtw_icon_selected
        )
        binding.trueOrFalse.setup(
            title = getString(R.string.ROW),
            iconNormalRes = R.drawable.game_row_icon_normal,
            iconSelectedRes = R.drawable.game_row_icon_selected
        )
        binding.multiChoise.setup(
            title = getString(R.string.MC),
            iconNormalRes = R.drawable.game_mc_icon_normal,
            iconSelectedRes = R.drawable.game_mc_icon_selected
        )
        binding.writeTheWord.setup(
            title = getString(R.string.WTW),
            iconNormalRes = R.drawable.game_wtw_icon_normal,
            iconSelectedRes = R.drawable.game_wtw_icon_selected
        )

        binding.match6.setOnClickListener {
            selectOnly(binding.match6)
            Log.d("DEBUG", "click: m6")
            goToGameSettings(GameType.MATCH8)
        }
        binding.trueOrFalse.setOnClickListener {
            selectOnly(binding.trueOrFalse)
            goToGameSettings(GameType.TRUEorFALSE)
        }
        binding.multiChoise.setOnClickListener {
            selectOnly(binding.multiChoise)
            goToGameSettings(GameType.OneOfFour)
        }
        binding.writeTheWord.setOnClickListener {
            selectOnly(binding.writeTheWord)
            goToGameSettings(GameType.WriteTheWord)
        }
    }

    private fun selectOnly(selected: View) {
        binding.match6.setSelectedState(selected === binding.match6)
        binding.trueOrFalse.setSelectedState(selected === binding.trueOrFalse)
        binding.multiChoise.setSelectedState(selected === binding.multiChoise)
        binding.writeTheWord.setSelectedState(selected === binding.writeTheWord)
    }

    private fun setupObservers() {
        binding.languageSelect.setOnClickListener { showToast() }
    }

    private fun showToast() {
        Toast.makeText(requireContext(), "game under construction", Toast.LENGTH_SHORT)
            .show()
    }

    private fun goToGameSettings(gameType: GameType) {
        Log.d("DEBUG", "goToGameSettings: $gameType")
        val dir = GameSelectFragmentDirections
            .actionGameSelectFragmentToGameFragment(gameType)
        findNavController().navigate(dir)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}