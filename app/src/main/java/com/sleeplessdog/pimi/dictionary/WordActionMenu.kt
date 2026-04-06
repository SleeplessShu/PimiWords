package com.sleeplessdog.pimi.dictionary

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.sleeplessdog.pimi.R
import com.sleeplessdog.pimi.dictionary.group_screen.GroupType
import com.sleeplessdog.pimi.utils.Gray05

@Composable
fun WordActionsMenu(
    expanded: Boolean,
    groupType: GroupType,
    onDismiss: () -> Unit,
    onEditWordClick: () -> Unit,
    onMoveWordClick: () -> Unit,
    onDeleteWordClick: () -> Unit,
    onSaveToSavedWordsClick: () -> Unit,
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(12.dp),
        containerColor = Gray05,
        tonalElevation = 6.dp,
        shadowElevation = 8.dp,
    ) {

        if (groupType == GroupType.USER) {

            ActionMenuItem(
                icon = R.drawable.icon_edit, titleId = R.string.words_edit, onClick = {
                    onEditWordClick()
                    onDismiss()
                })

            ActionMenuItem(
                icon = R.drawable.icon_move, titleId = R.string.words_move, onClick = {
                    onMoveWordClick()
                    onDismiss()
                })

            ActionMenuItem(
                icon = R.drawable.icon_delete, titleId = R.string.button_delete, onClick = {
                    onDismiss()
                    onDeleteWordClick()
                })
        } else {
            ActionMenuItem(
                icon = R.drawable.ic_group_saved, titleId = R.string.save_words, onClick = {
                    onSaveToSavedWordsClick()
                    onDismiss()
                })
        }
    }
}
