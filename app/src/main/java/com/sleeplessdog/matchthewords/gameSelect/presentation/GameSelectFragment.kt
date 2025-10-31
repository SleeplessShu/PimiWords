package com.sleeplessdog.matchthewords.gameSelect.presentation

import android.animation.ValueAnimator
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.animation.doOnEnd
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.sleeplessdog.matchthewords.R
import com.sleeplessdog.matchthewords.databinding.GameSelectV2FragmentBinding
import com.sleeplessdog.matchthewords.game.presentation.models.GameType
import com.sleeplessdog.matchthewords.game.presentation.models.Language
import com.sleeplessdog.matchthewords.gameSelect.controller.LanguageAdapter
import com.sleeplessdog.matchthewords.gameSelect.controller.toFlagLargeRes
import com.sleeplessdog.matchthewords.gameSelect.controller.toFlagSmallRes
import org.koin.androidx.viewmodel.ext.android.viewModel

class GameSelectFragment : Fragment() {
    private val viewModel: GameSelectViewModel by viewModel()
    private var _binding: GameSelectV2FragmentBinding? = null
    private val binding: GameSelectV2FragmentBinding get() = _binding!!
    private lateinit var langAdapter: LanguageAdapter

    // текущий UI язык и язык игры (то, что надо исключить)
    private var currentUiLang: Language = Language.RUSSIAN
    private var currentGameLang: Language = Language.SPANISH
    private var isLangShown = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = GameSelectV2FragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupGameCards()
        setupLanguageList()
        setupObservers()
    }

    private fun setupGameCards() {
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

    private fun setupLanguageList() {
        langAdapter = LanguageAdapter { picked ->
            currentUiLang = picked
            binding.languageSelect.setImageResource(picked.toFlagLargeRes())
            toggleLanguages(false)
        }

        binding.rvLanguages.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = langAdapter
        }

        langAdapter.submit(
            all = Language.entries,
            selectedLang = null,
            gameLang = currentGameLang
        )
    }

    private fun setupObservers() {
        binding.languageSelect.setOnClickListener {
            val isVisible = binding.rvLanguages.visibility == View.VISIBLE
            toggleLanguages(!isVisible)
        }

        binding.languagesBackground.setOnClickListener {
            toggleLanguages(false)
        }
        binding.languageSelect.setOnClickListener {
            toggleLanguages(!isLangShown)
        }

        /*binding.logo.setOnLongClickListener {
            true
            showToast()
        }*/
    }

    private fun toggleLanguages(show: Boolean) {
        val bg = binding.languagesBackground
        val rv = binding.rvLanguages

        if (show) {
            if (isLangShown) return
            isLangShown = true

            // обновляем список (фильтруем текущий и игровой)
            langAdapter.submit(
                all = Language.entries,
                selectedLang = currentUiLang,
                gameLang = currentGameLang
            )

            // фон
            bg.visibility = View.VISIBLE
            bg.alpha = 0f
            bg.animate()
                .alpha(1f)
                .setDuration(200)
                .start()

            // список
            rv.visibility = View.VISIBLE
            rv.alpha = 0f
            rv.scaleY = 0f
            rv.pivotY = 0f // раскрываем сверху
            rv.animate()
                .alpha(1f)
                .scaleY(1f)
                .setDuration(200)
                .withEndAction {
                    // после анимации ничего не надо, он уже с нормальной высотой
                }
                .start()

        } else {
            if (!isLangShown) return
            isLangShown = false

            // список
            rv.animate()
                .alpha(0f)
                .scaleY(0f)
                .setDuration(200)
                .withEndAction {
                    rv.visibility = View.GONE
                }
                .start()

            // фон
            bg.animate()
                .alpha(0f)
                .setDuration(200)
                .withEndAction {
                    bg.visibility = View.GONE
                }
                .start()
        }
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