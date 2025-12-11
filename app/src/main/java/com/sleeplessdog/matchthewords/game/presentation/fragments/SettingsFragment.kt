package com.sleeplessdog.matchthewords.game.presentation.fragments

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.sleeplessdog.matchthewords.R
import com.sleeplessdog.matchthewords.databinding.SettingsFragmentBinding
import com.sleeplessdog.matchthewords.game.domain.models.LanguageLevel
import com.sleeplessdog.matchthewords.game.domain.models.LanguageLevelChips
import com.sleeplessdog.matchthewords.game.presentation.controller.DifficultyCardController
import com.sleeplessdog.matchthewords.game.presentation.controller.FeaturedCategoriesController
import com.sleeplessdog.matchthewords.game.presentation.controller.LanguageAdapter
import com.sleeplessdog.matchthewords.game.presentation.controller.LanguageLevelController
import com.sleeplessdog.matchthewords.game.presentation.controller.LanguageMenuController
import com.sleeplessdog.matchthewords.game.presentation.controller.TopicsMenuController
import com.sleeplessdog.matchthewords.game.presentation.controller.toFlagLargeRes
import com.sleeplessdog.matchthewords.game.presentation.models.LanguageAdapterState
import com.sleeplessdog.matchthewords.game.presentation.models.TopicsMenuViews
import com.sleeplessdog.matchthewords.utils.ConstantsApp
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : Fragment(R.layout.settings_fragment) {

    private val vm: SettingsViewModel by viewModel()
    private var _binding: SettingsFragmentBinding? = null
    private val binding get() = _binding!!

    private var currentLangMode: LanguageAdapterState = LanguageAdapterState.STUDY

    private lateinit var langAdapter: LanguageAdapter
    private lateinit var languageMenuController: LanguageMenuController
    private lateinit var levelController: LanguageLevelController
    private lateinit var difficultyController: DifficultyCardController
    private lateinit var topicsController: TopicsMenuController
    private lateinit var featuredController: FeaturedCategoriesController

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = SettingsFragmentBinding.bind(view)
        setupLanguageMenuController()
        setupLevelController()
        setupDifficultyController()
        setupTopicsController()
        setupLanguageList()
        setupObservers()
    }

    private fun setupLanguageMenuController() {
        languageMenuController = LanguageMenuController(
            root = binding.languageSelectRoot,
            bg = binding.languagesBackground,
            bgSolid = binding.languagesBackgroundSolid,
            titleTv = binding.tvLanguageList
        )
    }

    private fun setupLevelController() {
        levelController = LanguageLevelController(
            chips = LanguageLevelChips(
                a1 = binding.btnA1,
                a2 = binding.btnA2,
                b1 = binding.btnB1,
                b2 = binding.btnB2,
                c1 = binding.btnC1,
                c2 = binding.btnC2
            ), onToggle = { level -> vm.toggleLevel(level) })
    }

    private fun setupDifficultyController() {
        difficultyController = DifficultyCardController(
            easy = binding.cardEasy,
            medium = binding.cardMedium,
            hard = binding.cardHard,
            expert = binding.cardExpert,
            onPick = { level -> vm.onDifficultyPicked(level) })
    }

    private fun setupTopicsController() {
        featuredController = FeaturedCategoriesController(
            chipGroup = binding.cgFeaturedCategories,
            onToggle = { key -> vm.onToggle(key) }
        )

        topicsController = TopicsMenuController(
            topicsMenuViews = TopicsMenuViews(
                root = binding.rootTopics,
                background = binding.topicsBackground,
                header = binding.header,
                categoriesScroll = binding.categoriesScroll,
                bottomButtons = binding.bottomButtons,
                groupUser = binding.cgUserCategories,
                groupDefault = binding.cgDefaultCategories,
                btnShowAll = binding.btnShowAllCategories,
                btnCancel = binding.btnCancel,
                btnSave = binding.btnSave
            ), getPreselectedKeys = {
                vm.state.value.user.plus(vm.state.value.defaults).filter { it.isSelected }
                    .map { it.key }.toSet()
            }, onSaveSelection = { keys -> vm.onSave(keys) },
            featuredController
        )
    }

    private fun setupLanguageList() {
        langAdapter = LanguageAdapter { picked ->
            vm.onLanguagePicked(picked, currentLangMode)
            langAdapter.setSelected(picked)
            binding.rvLanguageList.postDelayed({
                languageMenuController.hide()
            }, ConstantsApp.ANIMATION_DURATION_FOREGROUND)
        }

        binding.rvLanguageList.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = langAdapter
        }
    }

    private fun setupObservers() {
        observeUiState()
        observeEvents()
    }

    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            vm.state.collect { state ->
                featuredController.render(state.featured)
                topicsController.updateGroups(
                    user = state.user, defaults = state.defaults
                )
            }
        }

        vm.levels.observe(viewLifecycleOwner) { selected ->
            levelController.bindLevels(selected ?: emptySet<LanguageLevel>())
        }

        vm.difficulty.observe(viewLifecycleOwner) { level ->
            level?.let { difficultyController.bind(it) }
        }

        vm.studyLanguage.observe(viewLifecycleOwner) { study ->
            binding.ivFlagStudy.setImageResource(study.toFlagLargeRes())
        }

        vm.uiLanguage.observe(viewLifecycleOwner) { uiLang ->
            binding.ivFlagUi.setImageResource(uiLang.toFlagLargeRes())
        }

        vm.uiLanguageList.observe(viewLifecycleOwner) { list ->
            if (currentLangMode == LanguageAdapterState.UI && binding.rvLanguageList.isVisible) {
                val selected = vm.uiLanguage.value
                langAdapter.submit(list ?: emptyList(), selected)
                selected?.let { langAdapter.setSelected(it) }
            }
        }

        vm.studyLanguageList.observe(viewLifecycleOwner) { list ->
            if (currentLangMode == LanguageAdapterState.STUDY && binding.rvLanguageList.isVisible) {
                val selected = vm.studyLanguage.value
                langAdapter.submit(list ?: emptyList(), selected)
                selected?.let { langAdapter.setSelected(it) }
            }
        }
    }

    private fun observeEvents() {
        binding.ivFlagUi.setOnClickListener {
            languageMenuController.openMenuForMode(
                mode = LanguageAdapterState.UI,
                uiList = vm.uiLanguageList.value,
                studyList = vm.studyLanguageList.value,
                uiSelected = vm.uiLanguage.value,
                studySelected = vm.studyLanguage.value,
                adapter = langAdapter
            )
        }

        binding.ivFlagStudy.setOnClickListener {
            languageMenuController.openMenuForMode(
                mode = LanguageAdapterState.STUDY,
                uiList = vm.uiLanguageList.value,
                studyList = vm.studyLanguageList.value,
                uiSelected = vm.uiLanguage.value,
                studySelected = vm.studyLanguage.value,
                adapter = langAdapter
            )
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
