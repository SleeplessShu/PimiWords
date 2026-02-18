/*
package com.sleeplessdog.matchthewords.dictionary.dialogs

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
import com.sleeplessdog.matchthewords.dictionary.DialogType
import com.sleeplessdog.matchthewords.ui.theme.DarkTextDefault
import com.sleeplessdog.matchthewords.ui.theme.Gray05
import com.sleeplessdog.matchthewords.ui.theme.Gray07
import com.sleeplessdog.matchthewords.ui.theme.GreenPrimary
import com.sleeplessdog.matchthewords.ui.theme.textSize16SemiBold
import com.sleeplessdog.matchthewords.ui.theme.textSize20Medium
import com.sleeplessdog.matchthewords.ui.theme.textSize24Bold

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewGroupDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit,
    dialogType: DialogType,
) {
    var text by remember { mutableStateOf("") }
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
                Text(
                    text = stringResource(R.string.new_group),
                    style = textSize24Bold,
                    color = DarkTextDefault,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    placeholder = {
                        Text(
                            text = stringResource(R.string.enter_group_name),
                            style = textSize20Medium,
                            color = Gray07,
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    textStyle = textSize16SemiBold.copy(color = GreenPrimary),
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
                Spacer(modifier = Modifier.height(36.dp))
                DialogButtons(
                    onDismiss = onDismiss,
                    onConfirm = {
                        if (text.isNotBlank()) {
                            onConfirm(text)
                            onDismiss()
                        }

                    },
                    dialogType = dialogType
                )
            }
        }
    }
}*/
