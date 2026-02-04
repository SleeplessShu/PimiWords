package com.sleeplessdog.matchthewords.dictionary.user_group

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import org.koin.androidx.viewmodel.ext.android.viewModel

class UserGroupComposeFragment : Fragment() {
    private val viewModel: UserGroupViewModel by viewModel()
    private val args: UserGroupComposeFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val groupKey = args.groupKey

        return ComposeView(requireContext()).apply {
            setContent {
                UserGroupUi(
                    viewModelWords = viewModel,
                    onBackClick = { findNavController().navigateUp() },
                    groupKey = groupKey
                )
            }
        }
    }

}