package com.sleeplessdog.pimi.dictionary.dictionary_screen

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
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
import androidx.core.content.ContextCompat
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
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.sleeplessdog.pimi.R
import com.sleeplessdog.pimi.dictionary.DictionaryUi
import com.sleeplessdog.pimi.dictionary.group_screen.GroupType
import com.sleeplessdog.pimi.dictionary.group_screen.GroupUi
import com.sleeplessdog.pimi.dictionary.group_screen.GroupUiEvent
import com.sleeplessdog.pimi.dictionary.group_screen.GroupViewModel
import com.sleeplessdog.pimi.main.MainActivity
import com.sleeplessdog.pimi.utils.ConstantsPaths.TAG_AUTH
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
        // навигация на экран покупки
    }


    private fun showAuthDialog() {
        val dialog = androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.auth_dialog_title))
            .setMessage(getString(R.string.auth_dialog_message))
            .setPositiveButton(getString(R.string.auth_dialog_confirm)) { _, _ ->
                viewModelDictionary.clearPendingEvent()
                startGoogleSignIn()
            }
            .setNegativeButton(getString(R.string.auth_dialog_decline)) { _, _ ->
                viewModelDictionary.onAuthDeclined()
            }
            .setCancelable(false)
            .create()

        dialog.show()

        val green = ContextCompat.getColor(requireContext(), R.color.green_primary)
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(green)
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(green)
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
        Log.d(TAG_AUTH, "resultCode: ${result.resultCode}")

        // читаем результат независимо от resultCode
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            Log.d(TAG_AUTH, "account: ${account?.email}")
            Log.d(TAG_AUTH, "idToken: ${account?.idToken}")
            account?.idToken?.let {
                viewModelDictionary.onGoogleIdTokenReceived(it)
            } ?: Log.d(TAG_AUTH, "idToken is NULL")
        } catch (e: ApiException) {
            Log.d(TAG_AUTH, "ApiException statusCode: ${e.statusCode}")
            Log.d(TAG_AUTH, "ApiException message: ${e.message}")
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
