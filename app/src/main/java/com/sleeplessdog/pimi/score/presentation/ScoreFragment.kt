package com.sleeplessdog.pimi.score.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.sleeplessdog.pimi.score.domain.models.AwardsCatalog
import com.sleeplessdog.pimi.score.presentation.models.ScoreUiState
import com.sleeplessdog.pimi.utils.ConstantsPaths.NAV_AWARDS
import com.sleeplessdog.pimi.utils.ConstantsPaths.NAV_SCORE_MAIN
import org.koin.androidx.viewmodel.ext.android.viewModel


class ScoreFragment : Fragment() {
    private val viewModel: ScoreViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View? {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(
                ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed
            )
            setContent {
                val state by viewModel.state.collectAsState(
                    initial = ScoreUiState()
                )

                val composeNavController = rememberNavController()
                NavHost(
                    navController = composeNavController,
                    startDestination = NAV_SCORE_MAIN,
                    enterTransition = {
                        slideInHorizontally(
                            initialOffsetX = { fullWidth -> fullWidth }, animationSpec = tween(400)
                        ) + fadeIn(animationSpec = tween(400))
                    },
                    exitTransition = {
                        slideOutHorizontally(
                            targetOffsetX = { fullWidth -> -fullWidth }, animationSpec = tween(400)
                        ) + fadeOut(animationSpec = tween(400))
                    },
                    popEnterTransition = {
                        slideInHorizontally(
                            initialOffsetX = { fullWidth -> -fullWidth }, animationSpec = tween(400)
                        ) + fadeIn(animationSpec = tween(400))
                    },
                    popExitTransition = {
                        slideOutHorizontally(
                            targetOffsetX = { fullWidth -> fullWidth }, animationSpec = tween(400)
                        ) + fadeOut(animationSpec = tween(400))
                    }) {
                    composable(NAV_SCORE_MAIN) {
                        ScoreScreen(
                            state = state, navController = composeNavController
                        )
                    }
                    composable(NAV_AWARDS) {
                        AwardScreen(
                            awards = AwardsCatalog.all, navController = composeNavController
                        )
                    }
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
}
