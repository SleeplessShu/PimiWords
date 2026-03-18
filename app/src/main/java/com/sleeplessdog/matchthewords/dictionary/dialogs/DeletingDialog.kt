package com.sleeplessdog.matchthewords.dictionary.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.sleeplessdog.matchthewords.R
import com.sleeplessdog.matchthewords.dictionary.models.DialogType
import com.sleeplessdog.matchthewords.ui.theme.DarkTextDefault
import com.sleeplessdog.matchthewords.ui.theme.Gray05
import com.sleeplessdog.matchthewords.ui.theme.textSize16SemiBold
import com.sleeplessdog.matchthewords.ui.theme.textSize24Bold


@Composable
fun DeletingDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    title: String,
    dialogType: DialogType,
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
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
                Text(
                    text = stringResource(R.string.dialog_delete),
                    style = textSize24Bold,
                    color = DarkTextDefault,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(16.dp))
                val question = when (dialogType) {
                    DialogType.DELETE_GROUP -> stringResource(R.string.delete_group_confirm, title)
                    DialogType.DELETE_PAIR -> stringResource(R.string.delete_word_confirm, title)
                    else -> ""
                }
                Text(
                    text = question,
                    style = textSize16SemiBold,
                    color = DarkTextDefault.copy(alpha = 0.8f),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(36.dp))

                DialogButtons(
                    onDismiss = onDismiss,
                    onConfirm = onConfirm,
                    dialogType = dialogType
                )
            }
        }
    }
}
