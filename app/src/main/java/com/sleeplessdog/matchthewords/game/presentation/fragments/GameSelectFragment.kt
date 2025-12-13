package com.sleeplessdog.matchthewords.game.presentation.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.sleeplessdog.matchthewords.R
import com.sleeplessdog.matchthewords.databinding.GameSelectFragmentBinding
import com.sleeplessdog.matchthewords.game.presentation.controller.LanguageAdapter
import com.sleeplessdog.matchthewords.game.presentation.controller.LanguageMenuController
import com.sleeplessdog.matchthewords.game.presentation.controller.toFlagLargeRes
import com.sleeplessdog.matchthewords.game.presentation.models.GameType
import com.sleeplessdog.matchthewords.utils.ConstantsTimeReaction.LANGUAGE_LIST_CLOSE
import org.koin.androidx.viewmodel.ext.android.viewModel

class GameSelectFragment : Fragment(R.layout.game_select_fragment) {

    private val viewModel: GameSelectViewModel by viewModel()

    private var binding: GameSelectFragmentBinding? = null

    private lateinit var langAdapter: LanguageAdapter
    private lateinit var languageMenuController: LanguageMenuController
    private var isLangShown = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = GameSelectFragmentBinding.bind(view)
        setupLanguageManager()
        setupLanguageList()
        setupLanguageButton()
        setupGameCards()
        setupObservers()
    }

    private fun setupLanguageManager() {
        val b = requireNotNull(binding)
        languageMenuController = LanguageMenuController(
            root = b.languageSelectRoot,
            bg = b.languagesBackground,
            bgSolid = b.languagesBackgroundSolid,
            titleTv = b.tvLanguageList
        )
    }


    private fun setupLanguageList() {
        val b = requireNotNull(binding)
        langAdapter = LanguageAdapter { picked ->
            viewModel.onLanguagePicked(picked)
            langAdapter.setSelected(picked)

            binding?.rvLanguages?.postDelayed({
                languageMenuController.hide()
            }, LANGUAGE_LIST_CLOSE)
        }

        b.rvLanguages.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = langAdapter
        }
    }


    private fun setupGameCards() {
        val b = requireNotNull(binding)
        b.match6.setup(
            title = getString(R.string.MTW),
            iconNormalRes = R.drawable.ic_game_mtw_normal,
            iconSelectedRes = R.drawable.ic_game_mtw_selected
        )
        b.trueOrFalse.setup(
            title = getString(R.string.ROW),
            iconNormalRes = R.drawable.ic_game_row_normal,
            iconSelectedRes = R.drawable.ic_game_row_selected
        )
        b.multiChoise.setup(
            title = getString(R.string.MC),
            iconNormalRes = R.drawable.ic_game_mc_normal,
            iconSelectedRes = R.drawable.ic_game_mc_selected
        )
        b.writeTheWord.setup(
            title = getString(R.string.WTW),
            iconNormalRes = R.drawable.ic_game_wtw_normal,
            iconSelectedRes = R.drawable.ic_game_wtw_selected
        )

        b.match6.setOnClickListener {
            selectOnly(b.match6)
            viewModel.onGamePicked(GameType.MATCH8)
        }
        b.trueOrFalse.setOnClickListener {
            selectOnly(b.trueOrFalse)
            viewModel.onGamePicked(GameType.TRUEorFALSE)
        }
        b.multiChoise.setOnClickListener {
            selectOnly(b.multiChoise)
            viewModel.onGamePicked(GameType.OneOfFour)
        }
        b.writeTheWord.setOnClickListener {
            selectOnly(b.writeTheWord)
            viewModel.onGamePicked(GameType.WriteTheWord)
        }
    }

    private fun selectOnly(selected: View) {
        val b = requireNotNull(binding)
        b.match6.setSelectedState(selected === b.match6)
        b.trueOrFalse.setSelectedState(selected === b.trueOrFalse)
        b.multiChoise.setSelectedState(selected === b.multiChoise)
        b.writeTheWord.setSelectedState(selected === b.writeTheWord)
    }

    private fun setupObservers() {
        viewModel.availableLanguages.observe(viewLifecycleOwner) { langs ->
            val selected = viewModel.studyLanguage.value
            langAdapter.submit(langs, selected)
        }

        // выбранный язык (большой флаг)
        viewModel.studyLanguage.observe(viewLifecycleOwner) { study ->
            binding?.languageSelect?.setImageResource(study.toFlagLargeRes())
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
        binding?.languageSelect?.setOnClickListener {
            languageMenuController.show(R.string.std_language) {
                val list = viewModel.availableLanguages.value ?: emptyList()
                val selected = viewModel.studyLanguage.value
                langAdapter.submit(list, selected)
                selected?.let { langAdapter.setSelected(it) }
            }
        }
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }
}
