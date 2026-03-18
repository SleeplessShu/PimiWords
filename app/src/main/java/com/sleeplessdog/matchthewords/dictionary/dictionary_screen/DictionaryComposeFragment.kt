package com.sleeplessdog.matchthewords.dictionary.dictionary_screen

import android.app.Activity.RESULT_OK
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.sleeplessdog.matchthewords.R
import com.sleeplessdog.matchthewords.dictionary.DictionaryUi
import com.sleeplessdog.matchthewords.dictionary.group_screen.GroupType
import com.sleeplessdog.matchthewords.dictionary.group_screen.GroupUi
import com.sleeplessdog.matchthewords.dictionary.group_screen.GroupViewModel
import com.sleeplessdog.matchthewords.main.MainActivity
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class DictionaryComposeFragment : Fragment() {

    private val viewModelDictionary: DictionaryViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        return ComposeView(requireContext()).apply {

            val bottomPadding =
                (requireActivity() as MainActivity).getBottomNavHeight()

            setViewCompositionStrategy(
                ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed
            )

            setContent {

                val composeNavController = rememberNavController()

                NavHost(
                    navController = composeNavController,
                    startDestination = DictionaryDestinations.MAIN,

                    enterTransition = {
                        slideInHorizontally(
                            initialOffsetX = { it },
                            animationSpec = tween(400)
                        ) + fadeIn(tween(400))
                    },

                    exitTransition = {
                        slideOutHorizontally(
                            targetOffsetX = { -it },
                            animationSpec = tween(400)
                        ) + fadeOut(tween(400))
                    },

                    popEnterTransition = {
                        slideInHorizontally(
                            initialOffsetX = { -it },
                            animationSpec = tween(400)
                        ) + fadeIn(tween(400))
                    },

                    popExitTransition = {
                        slideOutHorizontally(
                            targetOffsetX = { it },
                            animationSpec = tween(400)
                        ) + fadeOut(tween(400))
                    }
                ) {

                    // =======================
                    // DICTIONARY SCREEN
                    // =======================
                    composable(DictionaryDestinations.MAIN) {

                        DictionaryUi(
                            viewModel = viewModelDictionary,

                            onNavigateToUserGroup = { groupId, groupTitle ->
                                composeNavController.navigate(
                                    DictionaryDestinations.groupRoute(
                                        groupId, groupTitle, GroupType.USER
                                    )
                                )
                            },

                            onNavigateToGlobalGroup = { groupId, groupTitle ->
                                composeNavController.navigate(
                                    DictionaryDestinations.groupRoute(
                                        groupId, groupTitle,
                                        GroupType.GLOBAL
                                    )
                                )
                            },
                            bottomPadding = bottomPadding
                        )
                    }

                    // =======================
                    // GROUP SCREEN
                    // =======================
                    composable(
                        route = "${DictionaryDestinations.GROUP}/" +
                                "{${DictionaryDestinations.ARG_GROUP_ID}}/" +
                                "{${DictionaryDestinations.ARG_GROUP_NAME}}/" +
                                "{${DictionaryDestinations.ARG_GROUP_TYPE}}"
                    ) { backStackEntry ->

                        val groupViewModel: GroupViewModel =
                            koinViewModel(viewModelStoreOwner = backStackEntry)

                        GroupUi(
                            onBackClick = { composeNavController.popBackStack() },
                            viewModel = groupViewModel
                        )
                    }
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModelDictionary.pendingEvent.collect { event ->
                    when (event) {
                        DictionaryUiEvent.RequestGoogleSignIn -> {
                            startGoogleSignIn()
                            viewModelDictionary.clearPendingEvent() // сбрасываем после обработки
                        }

                        null -> Unit
                    }
                }
            }
        }
    }

    private fun startGoogleSignIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build()

        val activity = requireActivity() as MainActivity
        val signInClient = GoogleSignIn.getClient(activity, gso)
        googleSignInLauncher.launch(signInClient.signInIntent)
    }

    private val googleSignInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            val account = task.result
            account?.idToken?.let {
                viewModelDictionary.onGoogleIdTokenReceived(it)
            }
        } else {
            val message = getString(
                R.string.error_auth_declined
            ) + result.resultCode
            Toast.makeText(
                requireContext(), message, Toast.LENGTH_SHORT
            ).show()
            viewModelDictionary.onAuthFailed()
        }
    }

    private fun resetAuth() {
        FirebaseAuth.getInstance().signOut()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
        val signInClient = GoogleSignIn.getClient(requireActivity(), gso)

        signInClient.signOut()
            .addOnCompleteListener(requireActivity()) {
                signInClient.revokeAccess()
                    .addOnCompleteListener(requireActivity()) {
                        Toast.makeText(requireContext(), "Logged out", Toast.LENGTH_SHORT).show()
                    }
            }
    }
}
