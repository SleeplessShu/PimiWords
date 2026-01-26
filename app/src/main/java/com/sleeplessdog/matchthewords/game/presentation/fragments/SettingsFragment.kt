package com.sleeplessdog.matchthewords.game.presentation.fragments

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.flexbox.FlexboxLayout
import com.google.android.material.chip.Chip
import com.sleeplessdog.matchthewords.R
import com.sleeplessdog.matchthewords.backend.domain.models.LanguageLevel
import com.sleeplessdog.matchthewords.databinding.ItemDifficultyCardBinding
import com.sleeplessdog.matchthewords.databinding.SettingsFragmentBinding
import com.sleeplessdog.matchthewords.game.presentation.controller.LanguageAdapter
import com.sleeplessdog.matchthewords.game.presentation.controller.PimiScrollViewAdapter
import com.sleeplessdog.matchthewords.game.presentation.controller.PimiScrollbarController
import com.sleeplessdog.matchthewords.game.presentation.controller.toFlagLargeRes
import com.sleeplessdog.matchthewords.game.presentation.models.CategoryUi
import com.sleeplessdog.matchthewords.game.presentation.models.DifficultLevel
import com.sleeplessdog.matchthewords.game.presentation.view.LanguageMenuManager
import com.sleeplessdog.matchthewords.main.MainActivity
import com.sleeplessdog.matchthewords.utils.SupportFunctions
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

enum class LanguageAdapterState {
    UI, STUDY
}

class SettingsFragment : Fragment(R.layout.settings_fragment) {

    private val vm: SettingsViewModel by viewModel()
    private var _binding: SettingsFragmentBinding? = null
    private val binding get() = _binding!!

    private var currentLangMode: LanguageAdapterState = LanguageAdapterState.STUDY

    private lateinit var chipA1: Chip
    private lateinit var chipA2: Chip
    private lateinit var chipB1: Chip
    private lateinit var chipB2: Chip
    private lateinit var chipC1: Chip
    private lateinit var chipC2: Chip

    private lateinit var cardEasy: ItemDifficultyCardBinding
    private lateinit var cardMedium: ItemDifficultyCardBinding
    private lateinit var cardHard: ItemDifficultyCardBinding
    private lateinit var cardExpert: ItemDifficultyCardBinding

    private lateinit var langAdapter: LanguageAdapter
    private lateinit var languageMenuManager: LanguageMenuManager
    private var pimiController: PimiScrollbarController? = null

    private var preselected: Set<String> = emptySet()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = SettingsFragmentBinding.bind(view)
        setupLanguageMenuManager()
        setupLevelChips()
        setupLanguageList()
        setupDifficultyCards()
        setupObservers()
    }

    private fun setupPimiThumbOnce() {
        if (pimiController != null) return
        if (_binding == null) return

        val scrollView = binding.categoriesScroll
        val thumb = binding.tumblerPimi
        val track = binding.pathPimi
        val scrollableAdapter = PimiScrollViewAdapter(scrollView)
        pimiController =
            PimiScrollbarController(scrollableAdapter, track, thumb).also { it.attach() }
    }

    private fun setupLanguageMenuManager() {
        languageMenuManager = LanguageMenuManager(
            root = binding.languageSelectRoot,
            bg = binding.languagesBackground,
            bgSolid = binding.languagesBackgroundSolid,
            titleTv = binding.tvLanguageList
        )
    }

    private fun setupLevelChips() {
        chipA1 = binding.btnA1
        chipA2 = binding.btnA2
        chipB1 = binding.btnB1
        chipB2 = binding.btnB2
        chipC1 = binding.btnC1
        chipC2 = binding.btnC2
    }

    private fun setupDifficultyCards() {
        cardEasy = binding.cardEasy
        cardMedium = binding.cardMedium
        cardHard = binding.cardHard
        cardExpert = binding.cardExpert

        with(binding.cardEasy) {
            ivIcon.setImageResource(R.drawable.ic_game_lightning_1)
            tvTitle.setText(R.string.difficulty_easy)
            tvSubtitle.setText(R.string.difficulty_easy_words)
            root.setOnClickListener { vm.onDifficultyPicked(DifficultLevel.EASY) }
        }

        with(binding.cardMedium) {
            ivIcon.setImageResource(R.drawable.ic_game_lightning_2)
            tvTitle.setText(R.string.difficulty_medium)
            tvSubtitle.setText(R.string.difficulty_medium_words)
            root.setOnClickListener { vm.onDifficultyPicked(DifficultLevel.MEDIUM) }
        }

        with(binding.cardHard) {
            ivIcon.setImageResource(R.drawable.ic_game_lightning_3)
            tvTitle.setText(R.string.difficulty_hard)
            tvSubtitle.setText(R.string.difficulty_hard_words)
            root.setOnClickListener { vm.onDifficultyPicked(DifficultLevel.HARD) }
        }

        with(binding.cardExpert) {
            ivIcon.setImageResource(R.drawable.ic_game_lightning_4)
            tvTitle.setText(R.string.difficulty_expert)
            tvSubtitle.setText(R.string.difficulty_expert_words)
            root.setOnClickListener { vm.onDifficultyPicked(DifficultLevel.EXPERT) }
        }

    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                vm.state.collect { state ->
                    renderFeatured(state.featured)
                    renderGroup(binding.cgUserCategories, state.user)
                    renderGroup(binding.cgDefaultCategories, state.defaults)
                }
            }
        }

        vm.levels.observe(viewLifecycleOwner) { selected ->
            chipA1.isChecked = LanguageLevel.A1 in selected
            chipA2.isChecked = LanguageLevel.A2 in selected
            chipB1.isChecked = LanguageLevel.B1 in selected
            chipB2.isChecked = LanguageLevel.B2 in selected
            chipC1.isChecked = LanguageLevel.C1 in selected
            chipC2.isChecked = LanguageLevel.C2 in selected
        }

        chipA1.setOnClickListener { vm.toggleLevel(LanguageLevel.A1) }
        chipA2.setOnClickListener { vm.toggleLevel(LanguageLevel.A2) }
        chipB1.setOnClickListener { vm.toggleLevel(LanguageLevel.B1) }
        chipB2.setOnClickListener { vm.toggleLevel(LanguageLevel.B2) }
        chipC1.setOnClickListener { vm.toggleLevel(LanguageLevel.C1) }
        chipC2.setOnClickListener { vm.toggleLevel(LanguageLevel.C2) }

        binding.ivFlagStudy.setOnClickListener {
            currentLangMode = LanguageAdapterState.STUDY // Запоминаем режим

            languageMenuManager.show(R.string.std_language) {
                val list = vm.studyLanguageList.value ?: emptyList()
                val selected = vm.studyLanguage.value
                langAdapter.submit(list, selected)
                selected?.let { langAdapter.setSelected(it) }
            }
        }

        binding.ivFlagUi.setOnClickListener {
            currentLangMode = LanguageAdapterState.UI // Запоминаем режим

            languageMenuManager.show(R.string.int_language) {
                val list = vm.uiLanguageList.value ?: emptyList()
                val selected = vm.uiLanguage.value
                langAdapter.submit(list, selected)
                selected?.let { langAdapter.setSelected(it) }
            }
        }

        binding.topicsBackground.setOnClickListener { hideTopicsMenu() }

        vm.difficulty.observe(viewLifecycleOwner) { level ->

            cardEasy.root.isChecked = (level == DifficultLevel.EASY)
            cardMedium.root.isChecked = (level == DifficultLevel.MEDIUM)
            cardHard.root.isChecked = (level == DifficultLevel.HARD)
            cardExpert.root.isChecked = (level == DifficultLevel.EXPERT)
        }

        binding.btnShowAllCategories.setOnClickListener {
            preselected = vm.state.value.user.plus(vm.state.value.defaults).filter { it.isSelected }
                .map { it.key }.toSet()
            showTopicsMenu()
        }

        binding.btnCancel.setOnClickListener { hideTopicsMenu() }
        binding.btnSave.setOnClickListener {
            val selectedKeys = readSelectedKeys()
            vm.onSave(selectedKeys)
            hideTopicsMenu()
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
                langAdapter.submit(list, selected)
                selected?.let { langAdapter.setSelected(it) }
            }
        }

        vm.studyLanguageList.observe(viewLifecycleOwner) { list ->
            if (currentLangMode == LanguageAdapterState.STUDY && binding.rvLanguageList.isVisible) {
                val selected = vm.studyLanguage.value
                langAdapter.submit(list, selected)
                selected?.let { langAdapter.setSelected(it) }
            }
        }
    }

    private fun setupLanguageList() {
        langAdapter = LanguageAdapter(requireContext()) { picked ->
            vm.onLanguagePicked(picked, currentLangMode)
            langAdapter.setSelected(picked)
            binding.rvLanguageList.postDelayed({
                languageMenuManager.hide()
            }, 150)
        }

        binding.rvLanguageList.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = langAdapter
        }
    }

    private fun renderFeatured(list: List<CategoryUi>) {
        val group = binding.cgFeaturedCategories
        group.removeAllViews()

        list.forEach { item ->
            val chip = SupportFunctions.createCategoryChip(group, item)

            chip.isChecked = item.isSelected
            chip.setOnClickListener { vm.onToggle(item.key) }

            group.addView(chip)
        }
    }

    private fun renderGroup(group: FlexboxLayout, items: List<CategoryUi>) {
        group.removeAllViews()
        items.forEach { item ->
            group.addView(SupportFunctions.createCategoryChip(group, item).apply {
                isChecked = item.key in preselected || item.isSelected
            })
        }
    }

    private fun readSelectedKeys(): Set<String> {
        fun collect(group: FlexboxLayout): List<String> =
            (0 until group.childCount).mapNotNull { i ->
                val child = group.getChildAt(i)
                val chip = child as? Chip ?: return@mapNotNull null
                val key = chip.tag as? String ?: chip.text.toString()
                if (chip.isChecked) key else null
            }
        return (collect(binding.cgUserCategories) + collect(binding.cgDefaultCategories)).toSet()
    }

    private fun showTopicsMenu() {
        val root = binding.rootTopics
        if (root.isVisible) return

        root.alpha = 0f
        root.visibility = View.VISIBLE
        root.animate().alpha(1f).setDuration(150).start()

        binding.topicsBackground.apply {
            alpha = 0f
            animate().alpha(1f).setDuration(200).start()
        }

        val contentViews = listOf(
            binding.header, binding.categoriesScroll, binding.bottomButtons
        )

        contentViews.forEach { view ->
            view.alpha = 0f
            view.translationY = 40f
            view.animate().alpha(1f).translationY(0f).setDuration(200).start()
        }
        (requireActivity() as? MainActivity)?.setBottomNavVisibility(false)
        setupPimiThumbOnce()
    }

    private fun hideTopicsMenu() {
        val root = binding.rootTopics
        if (root.visibility != View.VISIBLE) return

        binding.topicsBackground.animate().alpha(0f).setDuration(200).start()

        val contentViews = listOf(
            binding.header, binding.categoriesScroll, binding.bottomButtons
        )

        var finished = 0
        val total = contentViews.size

        contentViews.forEach { view ->
            view.animate().alpha(0f).translationY(40f).setDuration(200).withEndAction {
                finished++
                if (finished == total) {
                    root.visibility = View.GONE

                    contentViews.forEach { v ->
                        v.alpha = 1f
                        v.translationY = 0f
                    }
                    binding.topicsBackground.alpha = 1f
                }
            }.start()
        }
        (requireActivity() as? MainActivity)?.setBottomNavVisibility(true)
    }

    override fun onDestroyView() {
        _binding = null
        pimiController = null
        super.onDestroyView()
    }
}
