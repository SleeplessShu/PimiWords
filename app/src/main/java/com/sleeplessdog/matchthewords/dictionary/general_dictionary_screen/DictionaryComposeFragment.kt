package com.sleeplessdog.matchthewords.dictionary.general_dictionary_screen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import org.koin.androidx.viewmodel.ext.android.viewModel

class DictionaryComposeFragment : Fragment() {
    private val viewModel: DictionaryViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                DictionaryUi(viewModel = viewModel, onNavigateToNewGroup = ::navigateToNewGroup)
            }
        }
    }

    private fun navigateToNewGroup(key: String) {
        val action =
            DictionaryComposeFragmentDirections.actionWordsFragmentToNewGroupFragment(groupKey = key)
        findNavController().navigate(action)
    }
}