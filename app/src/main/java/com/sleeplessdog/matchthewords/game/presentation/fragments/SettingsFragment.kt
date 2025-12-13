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
import com.sleeplessdog.matchthewords.game.presentation.controller.MenuData
import com.sleeplessdog.matchthewords.game.presentation.controller.TopicsMenuController
import com.sleeplessdog.matchthewords.game.presentation.controller.toFlagLargeRes
import com.sleeplessdog.matchthewords.game.presentation.models.LanguageAdapterState
import com.sleeplessdog.matchthewords.game.presentation.models.TopicsMenuViews
import com.sleeplessdog.matchthewords.utils.ConstantsApp
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : Fragment(R.layout.settings_fragment) {

    private val vm: SettingsViewModel by viewModel()
    private var binding: SettingsFragmentBinding? = null

    private var currentLangMode: LanguageAdapterState = LanguageAdapterState.STUDY

    private lateinit var langAdapter: LanguageAdapter
    private lateinit var languageMenuController: LanguageMenuController
    private lateinit var levelController: LanguageLevelController
    private lateinit var difficultyController: DifficultyCardController
    private lateinit var topicsController: TopicsMenuController
    private lateinit var featuredController: FeaturedCategoriesController

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = SettingsFragmentBinding.bind(view)

        setupLanguageMenuController()
        setupLevelController()
        setupDifficultyController()
        setupTopicsController()
        setupLanguageList()
        setupObservers()
    }

    private fun setupLanguageMenuController() {
        val b = requireNotNull(binding)
        languageMenuController = LanguageMenuController(
            root = b.languageSelectRoot,
            bg = b.languagesBackground,
            bgSolid = b.languagesBackgroundSolid,
            titleTv = b.tvLanguageList
        )
    }

    private fun setupLevelController() {
        val b = requireNotNull(binding)
        levelController = LanguageLevelController(
            chips = LanguageLevelChips(
                a1 = b.btnA1,
                a2 = b.btnA2,
                b1 = b.btnB1,
                b2 = b.btnB2,
                c1 = b.btnC1,
                c2 = b.btnC2
            ), onToggle = { level -> vm.toggleLevel(level) })
    }

    private fun setupDifficultyController() {
        val b = requireNotNull(binding)
        difficultyController = DifficultyCardController(
            easy = b.cardEasy,
            medium = b.cardMedium,
            hard = b.cardHard,
            expert = b.cardExpert,
            onPick = { level -> vm.onDifficultyPicked(level) })
    }

    private fun setupTopicsController() {
        val b = requireNotNull(binding)
        featuredController = FeaturedCategoriesController(
            chipGroup = b.cgFeaturedCategories, onToggle = { key -> vm.onToggle(key) })

        topicsController = TopicsMenuController(
            topicsMenuViews = TopicsMenuViews(
                root = b.rootTopics,
                background = b.topicsBackground,
                header = b.header,
                categoriesScroll = b.categoriesScroll,
                bottomButtons = b.bottomButtons,
                groupUser = b.cgUserCategories,
                groupDefault = b.cgDefaultCategories,
                btnShowAll = b.btnShowAllCategories,
                btnCancel = b.btnCancel,
                btnSave = b.btnSave
            ), getPreselectedKeys = {
                vm.state.value.user.plus(vm.state.value.defaults).filter { it.isSelected }
                    .map { it.key }.toSet()
            }, onSaveSelection = { keys -> vm.onSave(keys) }, featuredController
        )
    }

    private fun setupLanguageList() {
        val b = requireNotNull(binding)
        langAdapter = LanguageAdapter { picked ->
            vm.onLanguagePicked(picked, currentLangMode)
            langAdapter.setSelected(picked)
            b.rvLanguageList.postDelayed({
                languageMenuController.hide()
            }, ConstantsApp.ANIMATION_DURATION_FOREGROUND)
        }

        b.rvLanguageList.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = langAdapter
        }
    }

    private fun setupObservers() {
        observeUiState()
        observeEvents()
    }

    private fun observeUiState() {
        val b = requireNotNull(binding)
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
            b.ivFlagStudy.setImageResource(study.toFlagLargeRes())
        }

        vm.uiLanguage.observe(viewLifecycleOwner) { uiLang ->
            b.ivFlagUi.setImageResource(uiLang.toFlagLargeRes())
        }

        vm.uiLanguageList.observe(viewLifecycleOwner) { list ->
            if (currentLangMode == LanguageAdapterState.UI && b.rvLanguageList.isVisible) {
                val selected = vm.uiLanguage.value
                langAdapter.submit(list ?: emptyList(), selected)
                selected?.let { langAdapter.setSelected(it) }
            }
        }

        vm.studyLanguageList.observe(viewLifecycleOwner) { list ->
            if (currentLangMode == LanguageAdapterState.STUDY && b.rvLanguageList.isVisible) {
                val selected = vm.studyLanguage.value
                langAdapter.submit(list ?: emptyList(), selected)
                selected?.let { langAdapter.setSelected(it) }
            }
        }
    }

    private fun observeEvents() {
        val b = requireNotNull(binding)
        b.ivFlagUi.setOnClickListener {
            languageMenuController.openMenuForMode(
                mode = LanguageAdapterState.UI, data = MenuData(
                    uiList = vm.uiLanguageList.value,
                    studyList = vm.studyLanguageList.value,
                    uiSelected = vm.uiLanguage.value,
                    studySelected = vm.studyLanguage.value,
                ), adapter = langAdapter
            )
        }

        b.ivFlagStudy.setOnClickListener {
            languageMenuController.openMenuForMode(
                mode = LanguageAdapterState.STUDY, data = MenuData(
                    uiList = vm.uiLanguageList.value,
                    studyList = vm.studyLanguageList.value,
                    uiSelected = vm.uiLanguage.value,
                    studySelected = vm.studyLanguage.value,
                ), adapter = langAdapter
            )
        }
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }
}
