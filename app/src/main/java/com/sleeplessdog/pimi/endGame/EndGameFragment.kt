package com.sleeplessdog.pimi.endGame

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.sleeplessdog.matchthewords.R
import com.sleeplessdog.matchthewords.databinding.EndGameFragmentBinding
import com.sleeplessdog.pimi.animation.LottieAnimationController
import com.sleeplessdog.pimi.games.presentation.GameFragmentDirections
import com.sleeplessdog.pimi.games.presentation.GameViewModel
import com.sleeplessdog.pimi.games.presentation.controller.PimiRecyclerViewAdapter
import com.sleeplessdog.pimi.games.presentation.controller.PimiScrollbarController
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class EndGameFragment : Fragment(R.layout.end_game_fragment) {
    private val parentViewModel: GameViewModel by sharedViewModel(owner = { requireParentFragment() })
    private val childViewModel: EndGameViewModel by viewModel()

    private var _binding: EndGameFragmentBinding? = null
    private val binding: EndGameFragmentBinding get() = _binding!!

    private lateinit var wordsAdapter: EndGameWordsAdapter
    private var pimiController: PimiScrollbarController? = null
    private val lottieController = LottieAnimationController()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View? {
        _binding = EndGameFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        pimiController = null
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
        setupUI()

    }

    private fun setupUI() {
        wordsAdapter = EndGameWordsAdapter { ids ->
            childViewModel.updateSelection(ids)
        }

        binding.actionWithWordsOverlayView.rvWords.adapter = wordsAdapter

        binding.btnReportWords.setOnClickListener {
            wordsAdapter.toggleSelectAll(false)
            childViewModel.reportAboutMistake()
        }

        binding.btnSaveWords.setOnClickListener {
            wordsAdapter.toggleSelectAll(false)
            childViewModel.saveWordsToUsersDictionary()
        }

        binding.actionWithWordsOverlayView.buttonBack.setOnClickListener { childViewModel.hideActions() }

        binding.actionWithWordsOverlayView.btnCancel.setOnClickListener { childViewModel.hideActions() }

        binding.actionWithWordsOverlayView.addAll.setOnClickListener {
            val isChecked = binding.actionWithWordsOverlayView.checkboxSelectAll.isChecked
            binding.actionWithWordsOverlayView.checkboxSelectAll.isChecked = !isChecked
            wordsAdapter.toggleSelectAll(!isChecked)
        }

        binding.bNewGame.setOnClickListener {
            returnToGameSelect()
        }
        binding.bRestart.setOnClickListener {
            parentViewModel.restartGame()
        }
        binding.bToSettings.setOnClickListener {
            goToSettings()
        }
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            childViewModel.isActionEnabled.collect { enabled ->
                binding.actionWithWordsOverlayView.btnSave.isEnabled = enabled
            }
        }

        parentViewModel.endGameStats.observe(viewLifecycleOwner) { stats ->
            wordsAdapter.submitPairs(stats.sessionPairs)

            binding.actionWithWordsOverlayView.rvWords.post {
                setupPimiThumbOnce()
            }

            val isWin = stats.isWin
            if (isWin) {
                showResult(
                    result = getString(R.string.end_game_phrase_win),
                    phrase = getString(R.string.eg_congrats),
                    animation = R.raw.animation_endgame_victory_260101,
                    background = R.raw.animation_bg_confeti,
                    stats = stats
                )
            } else {
                showResult(
                    result = getString(R.string.end_game_phrase_loose),
                    phrase = getString(R.string.eg_sorrow),
                    animation = R.raw.animation_endgame_fail_v2,
                    background = R.raw.animation_bg_rain,
                    stats = stats,
                )
            }
        }

        childViewModel.actionsWithWords.observe(viewLifecycleOwner) { event ->
            if (event == null) {
                binding.actionWithWordsOverlayView.root.isVisible = false
                return@observe
            }
            when (event.action) {
                EndGameWordsAction.REPORT_ABOUT_MISTAKE -> {
                    setupWordsView(
                        header = getString(R.string.report_words),
                        acceptButton = getString(R.string.report),
                        onAcceptClick = {
                            childViewModel.sendReport()
                            binding.btnReportWords.isEnabled = false
                            showSuccessSnackbar(getString(R.string.report_sent))
                        })
                }

                EndGameWordsAction.SAVE_WORDS_TO_USERS_DICTIONARY -> {
                    setupWordsView(
                        header = getString(R.string.add_to_dictionary),
                        acceptButton = getString(R.string.save),
                        onAcceptClick = {
                            childViewModel.saveSelectedWords()
                            binding.btnSaveWords.isEnabled = false
                            showSuccessSnackbar(getString(R.string.words_saved))
                        })
                }
            }
            binding.actionWithWordsOverlayView.root.isVisible = event.isVisible
            if (event.isVisible) {
                pimiController?.forceUpdate()
            }
        }
    }

    private fun setupPimiThumbOnce() {
        if (pimiController != null) return
        if (_binding == null) return

        val recyclerView = binding.actionWithWordsOverlayView.rvWords
        val thumb = binding.actionWithWordsOverlayView.tumblerPimi
        val track = binding.actionWithWordsOverlayView.pathPimi

        val scrollableAdapter = PimiRecyclerViewAdapter(recyclerView)

        pimiController =
            PimiScrollbarController(scrollableAdapter, track, thumb).also { it.attach() }
    }

    private fun setupWordsView(
        header: String,
        acceptButton: String,
        onAcceptClick: () -> Unit,
    ) {
        binding.actionWithWordsOverlayView.header.text = header
        binding.actionWithWordsOverlayView.btnSave.text = acceptButton
        binding.actionWithWordsOverlayView.btnSave.setOnClickListener {
            onAcceptClick()
        }
    }

    private fun showSuccessSnackbar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT)
            .setBackgroundTint(requireContext().getColor(R.color.green_primary))
            .setTextColor(requireContext().getColor(R.color.gray_02)).show()
    }

    private fun showResult(
        result: String,
        phrase: String,
        animation: Int,
        background: Int,
        stats: EndGameStats?,
    ) {
        val errors = stats?.mistakesCount
        val score = stats?.score
        val words = stats?.wordsCount

        binding.tvResult.text = result
        binding.tvPhrase.text = phrase
        binding.tvErrors.text = errors.toString()
        binding.tvScore.text = score.toString()
        binding.tvWords.text = words.toString()
        lottieController.playAndStopOnLastFrame(
            where = binding.animationIdleView, what = animation
        )
        lottieController.playLoopCut(
            what = background,
            where = binding.animationBgView,
            loop = true,
            cutFromStartFrames = 1,
            cutFromEndFrames = 1,
        )
    }

    private fun returnToGameSelect() {
        val dir = GameFragmentDirections.actionGameFragmentToGameSelectFragment()
        findNavController().navigate(dir)
    }

    private fun goToSettings() {
        val dir = GameFragmentDirections.actionGameFragmentToSettingsFragment()
        findNavController().navigate(dir)
    }
}



