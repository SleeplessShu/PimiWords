package com.sleeplessdog.matchthewords.game.presentation.fragments

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RawRes
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.lottie.LottieAnimationView
import com.sleeplessdog.matchthewords.R
import com.sleeplessdog.matchthewords.databinding.GameSelectFragmentBinding
import com.sleeplessdog.matchthewords.game.presentation.controller.LanguageAdapter
import com.sleeplessdog.matchthewords.game.presentation.controller.LanguageMenuManager
import com.sleeplessdog.matchthewords.game.presentation.controller.toFlagLargeRes
import com.sleeplessdog.matchthewords.game.presentation.controller.toLanguageSelectAnimation
import com.sleeplessdog.matchthewords.game.presentation.models.GameType
import com.sleeplessdog.matchthewords.main.MainActivity
import com.sleeplessdog.matchthewords.utils.LandingRepeatController.ALWAYS_SHOW_FIRST_LANDING
import org.koin.androidx.viewmodel.ext.android.viewModel

class GameSelectFragment : Fragment() {

    private val viewModel: GameSelectViewModel by viewModel()
    private var _binding: GameSelectFragmentBinding? = null
    private val binding get() = _binding!!

    private lateinit var langAdapter: LanguageAdapter
    private lateinit var landingLanguageAdapter: LanguageAdapter
    private lateinit var languageMenuManager: LanguageMenuManager
    private var isLangShown = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
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

                binding.landingFirstOverlayView.root.isVisible = false
                binding.landingFirstOverlayView.root.animate().alpha(0f).setDuration(300)
                    .withEndAction {
                        binding.landingFirstOverlayView.root.isVisible = false
                        binding.landingFirstOverlayView.root.alpha = 1f
                    }
                if (binding.landingLanguageOverlayView.root.isVisible) {
                    val pickedLanguageAnimation = picked.toLanguageSelectAnimation()
                    val animationFrom = binding.landingLanguageOverlayView.animationIdleView
                    val animationTo = binding.landingLanguageOverlayView.animationActionView
                    playPickedWithCrossfade(
                        resTo = pickedLanguageAnimation,
                        animationTo = animationTo,
                        animationFrom = animationFrom
                    )
                }
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

        viewModel.studyLanguage.observe(viewLifecycleOwner) { study ->
            binding.languageSelect.setImageResource(study.toFlagLargeRes())

            if (isLangShown) {
                langAdapter.setSelected(study)
            }
        }

        viewModel.uiLanguage.observe(viewLifecycleOwner) {
            // не реализовано
        }

        viewModel.showLanding.observe(viewLifecycleOwner) { shouldShow ->
            if (shouldShow) {
                Log.d("DEBUG", "shouldShow: $shouldShow ")
                (requireActivity() as? MainActivity)?.setBottomNavVisibility(!shouldShow)

                binding.landingFirstOverlayView.root.isVisible = shouldShow

                if (shouldShow) {
                    binding.landingFirstOverlayView.textView.text =
                        getString(R.string.landing_start_salut)
                    binding.landingFirstOverlayView.btn.text =
                        getString(R.string.landing_start_button)
                    activateCurtains()
                    binding.landingFirstOverlayView.animationIdleView.setAnimation(R.raw.jog_min)
                    binding.landingFirstOverlayView.animationViewCurtains.playAnimation()
                    binding.landingFirstOverlayView.animationIdleView.playAnimation()
                    binding.landingFirstOverlayView.btn.setOnClickListener {
                        showOverlayToLanguageSelect()
                    }
                }
            }
        }

        viewModel.navigateToGame.observe(viewLifecycleOwner) { type ->
            if (type != null) {
                val dir = GameSelectFragmentDirections.actionGameSelectFragmentToGameFragment(type)
                findNavController().navigate(dir)
                viewModel.onNavigateConsumed()
            }
        }
    }

    private fun activateCurtains() {
        binding.landingFirstOverlayView.animationViewCurtains.apply {
            setAnimation(R.raw.zanaves2)
            repeatCount = 0

            removeAllAnimatorListeners()
            addAnimatorListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    setFrame(maxFrame.toInt() - 5)
                    pauseAnimation()
                }
            })

            playAnimation()
        }
    }

    private fun showOverlayToLanguageSelect() {
        binding.landingLanguageOverlayView.root.isVisible = true
        startDefaultLoop()
        binding.rvLanguages
        val list = viewModel.availableLanguages.value ?: emptyList()
        val selected = viewModel.studyLanguage.value
        langAdapter.submit(list, selected)
        selected?.let { langAdapter.setSelected(it) }
        binding.landingLanguageOverlayView.rvLanguages.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = langAdapter
        }
    }

    private fun startDefaultLoop() = with(binding.landingLanguageOverlayView.animationIdleView) {
        setAnimation(R.raw.animation_base_loop)
        repeatCount = com.airbnb.lottie.LottieDrawable.INFINITE
        playAnimation()
    }

    private fun closeLanguageLanding() {
        binding.landingLanguageOverlayView.root.isVisible = false
        viewModel.onLandingShown(ALWAYS_SHOW_FIRST_LANDING)
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

    private fun playPickedWithCrossfade(
        @RawRes resTo: Int,
        animationFrom: LottieAnimationView,
        animationTo: LottieAnimationView,
    ) {
        animationTo.apply {
            isVisible = true
            alpha = 0f
            progress = 0f
            setAnimation(resTo)
            repeatCount = 0
        }

        animationTo.animate()
            .alpha(1f)
            .setDuration(120)
            .start()


        animationFrom.postDelayed({
            animationFrom.animate()
                .alpha(0f)
                .setDuration(80)
                .withEndAction {
                    animationFrom.pauseAnimation()
                    animationFrom.isVisible = false
                    animationFrom.alpha = 1f
                }
                .start()
        }, 90)

        animationTo.removeAllAnimatorListeners()
        animationTo.addAnimatorListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                if (binding.landingLanguageOverlayView.root.isVisible) {
                    closeLanguageLanding()
                }
                animationTo.removeAllAnimatorListeners()
                animationTo.isVisible = false
                animationTo.alpha = 0f
            }
        })

        animationTo.playAnimation()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
