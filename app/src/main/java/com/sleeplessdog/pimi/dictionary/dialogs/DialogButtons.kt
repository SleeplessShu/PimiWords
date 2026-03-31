package com.sleeplessdog.pimi.dictionary.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sleeplessdog.matchthewords.R
import com.sleeplessdog.pimi.dictionary.models.DialogType
import com.sleeplessdog.pimi.utils.Gray03
import com.sleeplessdog.pimi.utils.GreenPrimary
import com.sleeplessdog.pimi.utils.t3Text

@Composable
fun DialogButtons(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    dialogType: DialogType,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.End
    ) {

        TextButton(
            onClick = onDismiss
        ) {
            Text(
                text = stringResource(R.string.button_cancel_v2),
                color = Gray03,
                style = t3Text
            )
        }

        Spacer(Modifier.width(8.dp))

        TextButton(
            onClick = onConfirm
        ) {
            val onConfirmText = when (dialogType) {
                DialogType.NEW_PAIR -> R.string.button_save
                DialogType.EDIT_PAIR -> R.string.button_rename
                DialogType.MOVE_PAIR -> R.string.button_move
                DialogType.DELETE_PAIR -> R.string.button_delete
                DialogType.DELETE_GROUP -> R.string.button_delete
                DialogType.RENAME_GROUP -> R.string.button_rename
                DialogType.NEW_GROUP -> R.string.button_save
            }
            Text(
                text = stringResource(onConfirmText),
                color = GreenPrimary,
                style = t3Text
            )
        }
    }
}