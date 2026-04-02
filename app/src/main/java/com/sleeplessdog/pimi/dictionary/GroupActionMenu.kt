package com.sleeplessdog.pimi.dictionary

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.sleeplessdog.matchthewords.R
import com.sleeplessdog.pimi.utils.Gray05

@Composable
fun GroupActionsMenu(
    expanded: Boolean,
    onDismiss: () -> Unit,
    onRename: (() -> Unit)?,
    onDelete: (() -> Unit)?,
    onPlay: () -> Unit,
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(12.dp),
        containerColor = Gray05,
        tonalElevation = 6.dp,
        shadowElevation = 8.dp,
    ) {
        ActionMenuItem(
            icon = R.drawable.icon_play_circle, titleId = R.string.button_play, onClick = {
                onDismiss()
                onPlay()
            })
        onRename?.let {
            ActionMenuItem(
                icon = R.drawable.icon_edit, titleId = R.string.button_rename, onClick = {
                    onDismiss()
                    it()
                }
            )
        }
        onDelete?.let {
            ActionMenuItem(
                icon = R.drawable.icon_delete, titleId = R.string.button_delete, onClick = {
                    onDismiss()
                    it()
                })
        }
    }
}
