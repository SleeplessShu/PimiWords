package com.sleeplessdog.matchthewords.game.presentation

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.TouchDelegate
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.sleeplessdog.matchthewords.R
import com.sleeplessdog.matchthewords.databinding.GameFragmentBinding
import com.sleeplessdog.matchthewords.game.presentation.controller.LottieAnimationController
import com.sleeplessdog.matchthewords.game.presentation.fragments.EndGameFragment
import com.sleeplessdog.matchthewords.game.presentation.fragments.LoadingFragment
import com.sleeplessdog.matchthewords.game.presentation.ingameFragments.OneOfFourFragment
import com.sleeplessdog.matchthewords.game.presentation.ingameFragments.TrueOrFalseFragment
import com.sleeplessdog.matchthewords.game.presentation.ingameFragments.WordsMatchingFragment
import com.sleeplessdog.matchthewords.game.presentation.ingameFragments.WriteTheWordFragment
import com.sleeplessdog.matchthewords.game.presentation.models.GameState
import com.sleeplessdog.matchthewords.game.presentation.models.GameType
import com.sleeplessdog.matchthewords.game.presentation.models.LandingConditions
import com.sleeplessdog.matchthewords.game.presentation.parentControllers.HeartsController
import com.sleeplessdog.matchthewords.utils.LandingRepeatController.ALWAYS_SHOW_GAME_LANDING
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.math.roundToInt

class GameFragment : Fragment() {
    private lateinit var heartsController: HeartsController
    private val lottieController = LottieAnimationController()

    private val args: GameFragmentArgs by navArgs()
    private val viewModel: GameViewModel by viewModel()
    private var _binding: GameFragmentBinding? = null
    private val binding: GameFragmentBinding get() = _binding!!
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View? {
        _binding = GameFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val currentType = args.gameType
        viewModel.setGame(currentType)
        heartsController = HeartsController(
            listOf(binding.heart1, binding.heart2, binding.heart3)
        )
        setupBottomSheet()
        setupObservers()
    }

    private fun setupBottomSheet() {
        bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheetExit.root)
        bottomSheetBehavior.isHideable = true
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    binding.overlay.visibility = View.GONE
                } else {
                    binding.overlay.visibility = View.VISIBLE
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {

                val newAlpha = if (slideOffset < 0) {
                    slideOffset + 1
                } else {
                    1f
                }

                binding.overlay.alpha = newAlpha
            }
        })

        binding.overlay.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }


        binding.bottomSheetExit.btnStay.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }

        binding.bottomSheetExit.btnExit.setOnClickListener {
            returnToGameSelect()
        }
    }

    private fun setupObservers() {

        viewModel.gameState.observe(viewLifecycleOwner) { newState ->
            when (newState.state) {
                GameState.LOADING -> {
                    childFragmentManager.beginTransaction()
                        .replace(R.id.flFragmentContainer, LoadingFragment()).commit()
                    binding.tvHeader.setText(R.string.state_title_loading)
                    binding.statsBlock.isVisible = false
                    binding.buttonBack.isVisible = false
                }

                GameState.GAME -> {
                    launchGame()
                    binding.tvHeader.setText(R.string.empty)
                    binding.statsBlock.isVisible = true
                    binding.buttonBack.isVisible = true
                }

                GameState.END_OF_GAME -> {
                    childFragmentManager.beginTransaction()
                        .replace(R.id.flFragmentContainer, EndGameFragment()).commit()
                    binding.tvHeader.setText(R.string.empty)
                    binding.statsBlock.isVisible = false
                    binding.buttonBack.isVisible = false
                }
            }
            if (newState.landingConditions.shouldShow) {
                showLanding(newState.landingConditions)
            }
        }

        viewModel.statsState.observe(viewLifecycleOwner) { stats ->
            binding.tvScores.text = stats.score
            binding.progressBar.setSegments(stats.progressSegments)
            binding.progressBar.setProgress(stats.progress)
            setHearts(stats.lives)
        }

        binding.buttonBack.expandTouchAreaByFactor(6f)

        binding.buttonBack.setOnClickListener {
            val gameState = viewModel.gameState.value?.state ?: GameState.GAME
            when (gameState) {
                GameState.GAME -> viewModel.showGameExitQuestion()
                else -> returnToGameSelect()
            }
        }

        viewModel.showExitDialogEvent.observe(viewLifecycleOwner) {
            showExitBottomSheet()
        }


    }

    private fun showExitBottomSheet() {
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    private fun returnToGameSelect() {
        viewModel.resetAll()
        val dir = GameFragmentDirections.actionGameFragmentToGameSelectFragment()
        findNavController().navigate(dir)
    }

    private fun launchGame() {
        val gameType = viewModel.gameState.value?.gameType ?: GameType.MATCH8
        when (gameType) {
            GameType.MATCH8 -> {
                childFragmentManager.beginTransaction()
                    .replace(R.id.flFragmentContainer, WordsMatchingFragment()).commit()
            }

            GameType.TRUEorFALSE -> {
                childFragmentManager.beginTransaction()
                    .replace(R.id.flFragmentContainer, TrueOrFalseFragment()).commit()
            }

            GameType.OneOfFour -> {
                childFragmentManager.beginTransaction()
                    .replace(R.id.flFragmentContainer, OneOfFourFragment()).commit()
            }

            GameType.WriteTheWord -> childFragmentManager.beginTransaction()
                .replace(R.id.flFragmentContainer, WriteTheWordFragment()).commit()
        }
    }

    private fun setHearts(heartsQuantity: Int) {
        heartsController.render(heartsQuantity)
    }

    private fun showLanding(landingConditions: LandingConditions) {
        val showLanding = landingConditions.shouldShow
        val header = getString(landingConditions.headerTextId)
        val text = getString(landingConditions.regularTextId)
        binding.landingFirstOverlayView.root.isVisible = showLanding
        binding.landingFirstOverlayView.tvHeader.text = header
        binding.landingFirstOverlayView.tvText.text = text
        lottieController.playLoopCut(
            res = landingConditions.animation,
            view = binding.landingFirstOverlayView.animationIdleView,
            loop = true,
            cutFromStartFrames = 2,
            cutFromEndFrames = 2,
        )
        binding.landingFirstOverlayView.btnStart.setOnClickListener {
            viewModel.onLandingShown(ALWAYS_SHOW_GAME_LANDING, landingConditions.key)
            binding.landingFirstOverlayView.root.animate().alpha(0f).setDuration(300)
                .withEndAction {
                    binding.landingFirstOverlayView.root.isVisible = false
                    binding.landingFirstOverlayView.root.alpha = 1f
                }
        }

        binding.landingFirstOverlayView.btnSettings.setOnClickListener {
            viewModel.resetAll()
            val dir = GameFragmentDirections.actionGameFragmentToSettingsFragment()
            findNavController().navigate(dir)

        }
    }


    fun View.expandTouchAreaByFactor(factor: Float) {
        val parentView = parent as? View ?: return
        if (factor <= 1f) return
        parentView.post {
            val rect = Rect()
            getHitRect(rect)
            val addX = ((rect.width() * (factor - 1f)) / 2f).roundToInt()
            val addY = ((rect.height() * (factor - 1f)) / 2f).roundToInt()
            rect.inset(-addX, -addY)
            parentView.touchDelegate = TouchDelegate(rect, this)
        }
    }
}

