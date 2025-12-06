package com.sleeplessdog.matchthewords.game.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.sleeplessdog.matchthewords.R
import com.sleeplessdog.matchthewords.databinding.GameSelectFragmentBinding
import com.sleeplessdog.matchthewords.game.presentation.controller.LanguageAdapter
import com.sleeplessdog.matchthewords.game.presentation.controller.LanguageMenuManager
import com.sleeplessdog.matchthewords.game.presentation.controller.toFlagLargeRes
import com.sleeplessdog.matchthewords.game.presentation.models.GameType
import org.koin.androidx.viewmodel.ext.android.viewModel

class GameSelectFragment : Fragment() {

    private val viewModel: GameSelectViewModel by viewModel()
    private var _binding: GameSelectFragmentBinding? = null
    private val binding get() = _binding!!

    private lateinit var langAdapter: LanguageAdapter
    private lateinit var languageMenuManager: LanguageMenuManager
    private var isLangShown = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = GameSelectFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupLanguageList()
        setupLanguageButton()
        setupLanguageManager()
        setupGameCards()
        setupObservers()
    }

    private fun setupLanguageManager() {
        languageMenuManager = LanguageMenuManager(
            root = binding.languageSelectRoot,
            bg = binding.languagesBackground,
            bgSolid = binding.languagesBackgroundSolid,
            titleTv = binding.tvLanguageList
        )
    }


    private fun setupLanguageList() {
        langAdapter = LanguageAdapter { picked ->
            viewModel.onLanguagePicked(picked)
            langAdapter.setSelected(picked)

            binding.rvLanguages.postDelayed({
                languageMenuManager.hide()
            }, 150)
        }

        binding.rvLanguages.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = langAdapter
        }
    }


    private fun setupGameCards() {
        binding.match6.setup(
            title = getString(R.string.MTW),
            iconNormalRes = R.drawable.ic_game_mtw_normal,
            iconSelectedRes = R.drawable.ic_game_mtw_selected
        )
        binding.trueOrFalse.setup(
            title = getString(R.string.ROW),
            iconNormalRes = R.drawable.ic_game_row_normal,
            iconSelectedRes = R.drawable.ic_game_row_selected
        )
        binding.multiChoise.setup(
            title = getString(R.string.MC),
            iconNormalRes = R.drawable.ic_game_mc_normal,
            iconSelectedRes = R.drawable.ic_game_mc_selected
        )
        binding.writeTheWord.setup(
            title = getString(R.string.WTW),
            iconNormalRes = R.drawable.ic_game_wtw_normal,
            iconSelectedRes = R.drawable.ic_game_wtw_selected
        )

        binding.match6.setOnClickListener {
            selectOnly(binding.match6)
            viewModel.onGamePicked(GameType.MATCH8)
        }
        binding.trueOrFalse.setOnClickListener {
            selectOnly(binding.trueOrFalse)
            viewModel.onGamePicked(GameType.TRUEorFALSE)
        }
        binding.multiChoise.setOnClickListener {
            selectOnly(binding.multiChoise)
            viewModel.onGamePicked(GameType.OneOfFour)
        }
        binding.writeTheWord.setOnClickListener {
            selectOnly(binding.writeTheWord)
            viewModel.onGamePicked(GameType.WriteTheWord)
        }
    }

    private fun selectOnly(selected: View) {
        binding.match6.setSelectedState(selected === binding.match6)
        binding.trueOrFalse.setSelectedState(selected === binding.trueOrFalse)
        binding.multiChoise.setSelectedState(selected === binding.multiChoise)
        binding.writeTheWord.setSelectedState(selected === binding.writeTheWord)
    }

    private fun setupObservers() {
        viewModel.availableLanguages.observe(viewLifecycleOwner) { langs ->
            val selected = viewModel.studyLanguage.value
            langAdapter.submit(langs, selected)
        }

        // выбранный язык (большой флаг)
        viewModel.studyLanguage.observe(viewLifecycleOwner) { study ->
            binding.languageSelect.setImageResource(study.toFlagLargeRes())
            // можно тут же подсветить, если список открыт
            if (isLangShown) {
                langAdapter.setSelected(study)
            }
        }

        viewModel.uiLanguage.observe(viewLifecycleOwner) {
            // сейчас ты не показываешь UI-флаг — можно игнорить
        }

        viewModel.navigateToGame.observe(viewLifecycleOwner) { type ->
            if (type != null) {
                val dir = GameSelectFragmentDirections.actionGameSelectFragmentToGameFragment(type)
                findNavController().navigate(dir)
                viewModel.onNavigateConsumed()
            }
        }
    }

    private fun setupLanguageButton() {
        binding.languageSelect.setOnClickListener {
            languageMenuManager.show(R.string.std_language) {
                val list = viewModel.availableLanguages.value ?: emptyList()
                val selected = viewModel.studyLanguage.value
                langAdapter.submit(list, selected)
                selected?.let { langAdapter.setSelected(it) }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
