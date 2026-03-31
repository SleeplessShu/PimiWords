package com.sleeplessdog.pimi.dictionary.word_packs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sleeplessdog.matchthewords.R
import com.sleeplessdog.pimi.utils.BlackPrimary
import com.sleeplessdog.pimi.utils.DarkTextDefault
import com.sleeplessdog.pimi.utils.Gray05
import com.sleeplessdog.pimi.utils.GreenPrimary
import com.sleeplessdog.pimi.utils.t1Title
import com.sleeplessdog.pimi.utils.t2Title
import com.sleeplessdog.pimi.utils.t3Text
import com.sleeplessdog.pimi.utils.t4Text
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WordPacksOverlay(
    viewModel: WordPacksViewModel,
    onDismiss: () -> Unit,
) {
    val state by viewModel.state.collectAsState()
    val installState by viewModel.installState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadPacks()
    }

    LaunchedEffect(installState) {
        if (installState is WordPacksViewModel.InstallState.Success) {
            delay(2000)
            viewModel.resetInstallState()
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = BlackPrimary,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
        ) {
            Text(
                text = stringResource(R.string.word_packs_title),
                style = t1Title,
                color = DarkTextDefault,
                modifier = Modifier
                    .padding(bottom = 16.dp)
                    .align(Alignment.CenterHorizontally)
            )

            when (val s = state) {
                is WordPacksViewModel.State.Loading -> {
                    CircularProgressIndicator(
                        color = GreenPrimary,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }

                is WordPacksViewModel.State.Loaded -> {
                    if (s.packs.isEmpty()) {
                        Text(
                            text = stringResource(R.string.word_packs_empty),
                            style = t2Title,
                            color = DarkTextDefault.copy(alpha = 0.6f),
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    } else {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(s.packs) { pack ->
                                WordPackItem(
                                    pack = pack,
                                    isInstalling = installState is WordPacksViewModel.InstallState.Installing,
                                    onInstall = { viewModel.install(pack.fileName) })
                            }
                        }
                    }
                }

                is WordPacksViewModel.State.Error -> {
                    Text(
                        text = s.message,
                        color = Color.Red,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }

                else -> Unit
            }


            if (installState is WordPacksViewModel.InstallState.Success) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = stringResource(
                        R.string.word_pack_installed,
                        (installState as WordPacksViewModel.InstallState.Success).packName
                    ),
                    color = GreenPrimary,
                    style = t2Title,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}

@Composable
private fun WordPackItem(
    pack: WordPackUi,
    isInstalling: Boolean,
    onInstall: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Gray05, RoundedCornerShape(12.dp))
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = pack.name, style = t3Text, color = DarkTextDefault
            )
            Text(
                text = pluralStringResource(
                    R.plurals.words_count, pack.wordsCount, pack.wordsCount
                ), style = t4Text, color = DarkTextDefault.copy(alpha = 0.6f)
            )
        }

        Button(
            onClick = onInstall, enabled = !isInstalling, colors = ButtonDefaults.buttonColors(
                containerColor = GreenPrimary, contentColor = BlackPrimary
            ), shape = RoundedCornerShape(8.dp)
        ) {
            if (isInstalling) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp), color = BlackPrimary, strokeWidth = 2.dp
                )
            } else {
                Text(
                    text = stringResource(R.string.word_pack_install),
                    style = t3Text,
                    color = BlackPrimary
                )
            }
        }
    }
}