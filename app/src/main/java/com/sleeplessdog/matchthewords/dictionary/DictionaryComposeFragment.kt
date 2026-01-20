package com.sleeplessdog.matchthewords.dictionary

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import com.sleeplessdog.matchthewords.score.presentation.ScoreViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class DictionaryComposeFragment : Fragment() {
    private val viewModel: DictionaryViewModel by viewModel()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                DictionaryUi(viewModel = viewModel)
            }
        }
    }
}