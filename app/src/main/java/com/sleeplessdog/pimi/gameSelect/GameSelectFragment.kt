package com.sleeplessdog.pimi.gameSelect

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.sleeplessdog.matchthewords.R
import com.sleeplessdog.matchthewords.databinding.GameSelectFragmentBinding
import com.sleeplessdog.pimi.games.presentation.controller.LanguageAdapter
import com.sleeplessdog.pimi.animation.LottieAnimationController
import com.sleeplessdog.pimi.games.presentation.controller.toFlagLargeRes
import com.sleeplessdog.pimi.games.presentation.controller.toLanguageSelectAnimation
import com.sleeplessdog.pimi.games.presentation.models.GameType
import com.sleeplessdog.pimi.main.MainActivity
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class GameSelectFragment : Fragment() {
    private val args: GameSelectFragmentArgs by navArgs()

    private val viewModel: GameSelectViewModel by viewModel()
    private var _binding: GameSelectFragmentBinding? = null
    private val binding get() = _binding!!

    private lateinit var langAdapter: LanguageAdapter
    private lateinit var languageMenuManager: LanguageMenuManager
    private val lottieController = LottieAnimationController()
    private var isLangShown = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View {
        _binding = GameSelectFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val groupKey = args.groupKey.takeIf { it.isNotEmpty() }
        val isUser = args.groupIsUser
        viewModel.setForcedGroup(groupKey, isUser)

        setupLanguageList()
        setupLanguageButton()
        setupLanguageManager()
        setupGameCards()
        setupObservers()
    }

    /**
     * настройка менеджера выбора языка в выпадающем меню фрагмента
     */
    private fun setupLanguageManager() {
        languageMenuManager = LanguageMenuManager(
            root = binding.languageSelectRoot,
            bg = binding.languagesBackground,
            bgSolid = binding.languagesBackgroundSolid,
            titleTv = binding.tvLanguageList
        )
    }

    /**
     * настройка выбора языка во втором лэндинге
     */
    private fun setupLanguageList() {
        langAdapter = LanguageAdapter(requireContext()) { picked ->
            viewModel.onLanguagePicked(picked)
            langAdapter.setSelected(picked)

            binding.rvLanguages.postDelayed({

                binding.landingFirstOverlayView.root.isVisible = false
                binding.landingFirstOverlayView.root.animate().alpha(0f).setDuration(300)
                    .withEndAction {
                        binding.landingFirstOverlayView.root.isVisible = false
                        binding.landingFirstOverlayView.root.alpha = 1f
                    }
                if (binding.landingLanguageOverlayView.root.isVisible) {
                    val wearingHatAnimation = picked.toLanguageSelectAnimation()

                    playPimiWearHat(wearingHatAnimation)
                }
                languageMenuManager.hide()
            }, 150)
        }

        binding.rvLanguages.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = langAdapter
        }
    }

    /**
     * Настраивает содержимое кнопок выбора игры
     */
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

        binding.btnClearForcedGroup.setOnClickListener {
            viewModel.clearForcedGroup()
            updateGroupsDisplay()
        }

        viewModel.availableLanguages.observe(viewLifecycleOwner) { langs ->
            val selected = viewModel.studyLanguage.value
            langAdapter.submit(langs, selected)
        }

        viewModel.studyLanguage.observe(viewLifecycleOwner) { study ->
            binding.languageSelect.setImageResource(study.toFlagLargeRes())

            if (isLangShown) {
                langAdapter.setSelected(study)
            }
        }

        viewModel.forcedGroupTitle.observe(viewLifecycleOwner) {
            updateGroupsDisplay()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.selectedGroupTitles.collect {
                    updateGroupsDisplay()
                }
            }
        }

        viewModel.showLanding.observe(viewLifecycleOwner) { shouldShow ->
            if (shouldShow) {
                (requireActivity() as? MainActivity)?.setBottomNavVisibility(!shouldShow)

                binding.landingFirstOverlayView.root.isVisible = shouldShow

                if (shouldShow) {
                    binding.landingFirstOverlayView.textView.text =
                        getString(R.string.landing_start_salut)
                    binding.landingFirstOverlayView.btn.text =
                        getString(R.string.landing_start_button)
                    activateCurtains()
                    activatePimi()
                    binding.landingFirstOverlayView.btn.setOnClickListener {
                        showOverlayToLanguageSelect()
                    }
                }
            }
        }

        viewModel.navigateToGame.observe(viewLifecycleOwner) { event ->
            if (event != null) {
                val dir = GameSelectFragmentDirections.actionGameSelectFragmentToGameFragment(
                    gameType = event.gameType,
                    groupKey = event.groupKey ?: "",
                    groupIsUser = event.groupIsUser
                )
                findNavController().navigate(dir)
                viewModel.onNavigateConsumed()
            }
        }
    }

    /**
     * Проигрывает анимацию в первом лендинге для занавесок
     */
    private fun activateCurtains() {
        lottieController.switchBetweenTwo(
            resFrom = R.raw.animation_first_landing_curtains_v3,
            resTo = R.raw.animation_curtains_static_260104,
            fromView = binding.landingFirstOverlayView.animationViewCurtainsOpen,
            toView = binding.landingFirstOverlayView.animationViewCurtainsStatic,
            loopTo = true,
            toStartFrame = 10,
            cutFromEndFrames = 10
        )
    }

    /**
     * Проигрывает анимацию в первом лендинге для Пими
     */
    private fun activatePimi() {
        lottieController.switchBetweenTwo(
            resFrom = R.raw.animation_first_landing_jogging_260101,
            resTo = R.raw.animation_first_landing_jogging_loop_260101,
            fromView = binding.landingFirstOverlayView.animationActionView,
            toView = binding.landingFirstOverlayView.animationIdleView,
            loopTo = true,
        )
    }

    /**
     * Проигрывает анимацию во втором лендинге после клика по выбранному языку
     */
    private fun playPimiWearHat(wearingHatAnimation: Int) {
        lottieController.playUnderOnce(
            loopView = binding.landingLanguageOverlayView.animationActionView,
            underView = binding.landingLanguageOverlayView.animationIdleView,
            lottie2 = wearingHatAnimation,
            lottie2StartFrame = 6,
            onFinished = { closeLanguageLanding() })
    }

    /**
     * Показывает overlay выбора языка (2 лэндинг)
     */
    private fun showOverlayToLanguageSelect() {
        binding.landingLanguageOverlayView.root.isVisible = true
        binding.rvLanguages
        val list = viewModel.availableLanguages.value ?: emptyList()
        val selected = viewModel.studyLanguage.value
        langAdapter.submit(list, selected)
        selected?.let { langAdapter.setSelected(it) }
        binding.landingLanguageOverlayView.rvLanguages.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = langAdapter
        }
        binding.landingLanguageOverlayView.animationActionView.setAnimation(R.raw.animation_base_loop)
        binding.landingLanguageOverlayView.animationActionView.playAnimation()
    }

    /**
     * Закрывает overlay выбора языка (2 лэндинг)
     */
    private fun closeLanguageLanding() {
        binding.landingLanguageOverlayView.root.isVisible = false
        viewModel.onLandingShown()
        (requireActivity() as? MainActivity)?.setBottomNavVisibility(true)
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

    private fun updateGroupsDisplay() {
        val forcedTitle = viewModel.forcedGroupTitle.value

        if (forcedTitle != null) {
            binding.tvGroupsLabel.text = getString(R.string.game_group_label)
            binding.tvGroupsList.text = forcedTitle
            binding.btnClearForcedGroup.visibility = View.VISIBLE
        } else {
            binding.btnClearForcedGroup.visibility = View.GONE
            val titles = viewModel.selectedGroupTitles.value
            if (titles.isEmpty()) {
                binding.tvGroupsLabel.text = getString(R.string.game_no_groups_selected)
                binding.tvGroupsList.text = ""
            } else {
                binding.tvGroupsLabel.text = getString(R.string.selected_groups_label)
                binding.tvGroupsList.text = titles.joinToString("\n")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
