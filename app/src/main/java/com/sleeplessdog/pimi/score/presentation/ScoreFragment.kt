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
                    startDestination = "score_main",
                    enterTransition = {
                        // Новый экран заезжает справа налево
                        slideInHorizontally(
                            initialOffsetX = { fullWidth -> fullWidth },
                            animationSpec = tween(400)
                        ) + fadeIn(animationSpec = tween(400))
                    },
                    exitTransition = {
                        // Старый экран уезжает влево
                        slideOutHorizontally(
                            targetOffsetX = { fullWidth -> -fullWidth },
                            animationSpec = tween(400)
                        ) + fadeOut(animationSpec = tween(400))
                    },
                    popEnterTransition = {
                        // При нажатии "Назад" старый экран заезжает слева направо
                        slideInHorizontally(
                            initialOffsetX = { fullWidth -> -fullWidth },
                            animationSpec = tween(400)
                        ) + fadeIn(animationSpec = tween(400))
                    },
                    popExitTransition = {
                        // При нажатии "Назад" текущий экран уезжает вправо
                        slideOutHorizontally(
                            targetOffsetX = { fullWidth -> fullWidth },
                            animationSpec = tween(400)
                        ) + fadeOut(animationSpec = tween(400))
                    }
                ) {
                    composable("score_main") {
                        ScoreScreen(
                            state = state,
                            navController = composeNavController
                        )
                    }
                    composable("awards") {
                        AwardScreen(
                            awards = AwardsCatalog.all,
                            navController = composeNavController
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