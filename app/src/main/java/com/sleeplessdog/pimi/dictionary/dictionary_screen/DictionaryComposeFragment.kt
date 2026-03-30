package com.sleeplessdog.pimi.dictionary.dictionary_screen

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
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.sleeplessdog.matchthewords.R
import com.sleeplessdog.pimi.dictionary.DictionaryUi
import com.sleeplessdog.pimi.dictionary.group_screen.GroupType
import com.sleeplessdog.pimi.dictionary.group_screen.GroupUi
import com.sleeplessdog.pimi.dictionary.group_screen.GroupUiEvent
import com.sleeplessdog.pimi.dictionary.group_screen.GroupViewModel
import com.sleeplessdog.pimi.main.MainActivity
import com.sleeplessdog.pimi.utils.DictionaryDestinations
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class DictionaryComposeFragment : Fragment() {

    private val viewModelDictionary: DictionaryViewModel by viewModel()
    private val viewModelGroups: GroupViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        return ComposeView(requireContext()).apply {

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
                            initialOffsetX = { it }, animationSpec = tween(400)
                        ) + fadeIn(tween(400))
                    },

                    exitTransition = {
                        slideOutHorizontally(
                            targetOffsetX = { -it }, animationSpec = tween(400)
                        ) + fadeOut(tween(400))
                    },

                    popEnterTransition = {
                        slideInHorizontally(
                            initialOffsetX = { -it }, animationSpec = tween(400)
                        ) + fadeIn(tween(400))
                    },

                    popExitTransition = {
                        slideOutHorizontally(
                            targetOffsetX = { it }, animationSpec = tween(400)
                        ) + fadeOut(tween(400))
                    }) {

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
                                        groupId, groupTitle, GroupType.GLOBAL
                                    )
                                )
                            },
                            onPlayGroup = { groupKey, isUser ->
                                navigateToGameSelect(groupKey, isUser)
                            },
                        )
                    }

                    // =======================
                    // GROUP SCREEN
                    // =======================
                    composable(
                        route = "${DictionaryDestinations.GROUP}/" + "{${DictionaryDestinations.ARG_GROUP_ID}}/" + "{${DictionaryDestinations.ARG_GROUP_NAME}}/" + "{${DictionaryDestinations.ARG_GROUP_TYPE}}"
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
                            showAuthDialog()
                            viewModelDictionary.clearPendingEvent()
                        }

                        DictionaryUiEvent.ResetGoogleSignIn -> {
                            resetAuth()
                            viewModelDictionary.clearPendingEvent()
                        }

                        DictionaryUiEvent.ShowPremiumDialog -> {
                            showPremiumDialog()
                            viewModelDictionary.clearPendingEvent()
                        }

                        null -> Unit
                    }
                }
                viewModelGroups.pendingEvent.collect { event ->
                    when (event) {
                        GroupUiEvent.ShowPremiumDialog -> {
                            showPremiumDialogGroups()
                            viewModelGroups.clearPendingEvent()
                        }

                        null -> Unit
                    }
                }
            }
        }
    }

    private fun showPremiumDialog() {
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.premium_dialog_title))
            .setMessage(getString(R.string.premium_dialog_message))
            .setPositiveButton(getString(R.string.premium_dialog_confirm)) { _, _ ->
                navigateToPremium()
            }.setNegativeButton(getString(R.string.premium_dialog_decline)) { dialog, _ ->
                dialog.dismiss()
            }.show()
    }

    private fun showPremiumDialogGroups() {
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.premium_dialog_title))
            .setMessage(getString(R.string.premium_dialog_message_groups))
            .setPositiveButton(getString(R.string.premium_dialog_confirm)) { _, _ ->
                navigateToPremium()
            }.setNegativeButton(getString(R.string.premium_dialog_decline)) { dialog, _ ->
                dialog.dismiss()
            }.show()
    }

    private fun navigateToPremium() {
        // навигация на экран покупки — добавишь когда будет готов экран
        // findNavController().navigate(R.id.action_to_premiumFragment)
    }


    private fun showAuthDialog() {
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.auth_dialog_title))
            .setMessage(getString(R.string.auth_dialog_message))
            .setPositiveButton(getString(R.string.auth_dialog_confirm)) { _, _ ->
                viewModelDictionary.clearPendingEvent()
                startGoogleSignIn()
            }.setNegativeButton(getString(R.string.auth_dialog_decline)) { _, _ ->
                viewModelDictionary.onAuthDeclined()
            }.setCancelable(false).show()
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

        signInClient.signOut().addOnCompleteListener(requireActivity()) {
            signInClient.revokeAccess().addOnCompleteListener(requireActivity()) {
                Toast.makeText(requireContext(), "Logged out", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun navigateToGameSelect(groupKey: String, isUser: Boolean) {
        val action =
            DictionaryComposeFragmentDirections.actionDictionaryComposeFragmentToGameSelectFragment(
                groupKey = groupKey, groupIsUser = isUser
            )
        findNavController().navigate(action)
    }
}
