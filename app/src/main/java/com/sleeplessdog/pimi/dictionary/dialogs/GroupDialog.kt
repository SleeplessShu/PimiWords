package com.sleeplessdog.pimi.dictionary.dialogs

import androidx.compose.foundation.background
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
import com.sleeplessdog.pimi.R
import com.sleeplessdog.pimi.dictionary.models.DialogType
import com.sleeplessdog.pimi.utils.DarkTextDefault
import com.sleeplessdog.pimi.utils.Gray05
import com.sleeplessdog.pimi.utils.GreenPrimary
import com.sleeplessdog.pimi.utils.t1Title
import com.sleeplessdog.pimi.utils.t3Text


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupDialog(
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit,
    dialogType: DialogType,
    groupTitle: String = "",
) {
    var text by remember { mutableStateOf(groupTitle) }

    Dialog(
        onDismissRequest = onDismiss, properties = DialogProperties(
            usePlatformDefaultWidth = false, decorFitsSystemWindows = false
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
                    .padding(16.dp)
            ) {
                val message = when (dialogType) {
                    DialogType.NEW_GROUP -> stringResource(R.string.enter_group_name)
                    DialogType.RENAME_GROUP -> stringResource(
                        R.string.rename_group_confirm, groupTitle
                    )

                    else -> ""
                }
                Text(
                    text = message,
                    style = t1Title,
                    color = DarkTextDefault,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(16.dp))

                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = t3Text.copy(color = DarkTextDefault),
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
                    thickness = 1.dp, color = DarkTextDefault
                )

                Spacer(Modifier.height(36.dp))

                DialogButtons(
                    onDismiss = onDismiss, onConfirm = {
                        if (text.isNotBlank()) {
                            onConfirm(text.trim())
                        }
                    }, dialogType = dialogType
                )
            }
        }
    }
}