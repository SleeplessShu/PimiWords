package com.sleeplessdog.pimi.dictionary.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.sleeplessdog.matchthewords.R
import com.sleeplessdog.pimi.dictionary.group_screen.WordUi
import com.sleeplessdog.pimi.dictionary.models.DialogType
import com.sleeplessdog.pimi.utils.DarkTextDefault
import com.sleeplessdog.pimi.utils.Gray03
import com.sleeplessdog.pimi.utils.Gray05
import com.sleeplessdog.pimi.utils.Gray07
import com.sleeplessdog.pimi.utils.GreenPrimary
import com.sleeplessdog.pimi.utils.t1Title
import com.sleeplessdog.pimi.utils.t2Title
import com.sleeplessdog.pimi.utils.t3Text


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WordsPairDialog(
    dialogType: DialogType,
    word: WordUi = WordUi(id = 0L, word = "", translation = ""),
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit,
) {
    var origin by remember { mutableStateOf("") }
    var translate by remember { mutableStateOf("") }
    Dialog(
        onDismissRequest = onDismiss, properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false,
        )
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Gray05)
                    .padding(16.dp), verticalArrangement = Arrangement.Top
            ) {
                val text = when (dialogType) {
                    DialogType.NEW_PAIR -> R.string.add_word
                    DialogType.EDIT_PAIR -> R.string.edit_word
                    else -> 0
                }
                Text(
                    text = stringResource(text),
                    style = t1Title,
                    color = DarkTextDefault,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = origin,
                    onValueChange = { origin = it },
                    placeholder = {
                        val text = when (dialogType) {
                            DialogType.NEW_PAIR -> stringResource(R.string.enter_word_origin)
                            DialogType.EDIT_PAIR -> word.word
                            else -> ""
                        }
                        Text(
                            text = text,
                            style = t2Title,
                            color = Gray07,
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    textStyle = t3Text.copy(color = GreenPrimary),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,

                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        cursorColor = GreenPrimary
                    )
                )
                Divider(
                    thickness = 1.dp,
                    color = Gray03,
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp)
                )

                OutlinedTextField(
                    value = translate,
                    onValueChange = { translate = it },
                    placeholder = {
                        val text = when (dialogType) {
                            DialogType.NEW_PAIR -> stringResource(R.string.enter_word_translate)
                            DialogType.EDIT_PAIR -> word.translation
                            else -> ""
                        }
                        Text(
                            text = text,
                            style = t2Title,
                            color = Gray07,
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    textStyle = t3Text.copy(color = GreenPrimary),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,

                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        cursorColor = GreenPrimary
                    )
                )
                Divider(
                    thickness = 1.dp,
                    color = Gray03,
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                DialogButtons(
                    onDismiss = onDismiss, onConfirm = {
                        if (origin.isNotBlank() && translate.isNotBlank()) {
                            onConfirm(origin, translate)
                            onDismiss()
                        }
                    }, dialogType = dialogType
                )
            }
        }
    }
}