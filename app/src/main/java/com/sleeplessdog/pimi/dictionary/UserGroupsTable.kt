package com.sleeplessdog.pimi.dictionary

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.sleeplessdog.pimi.R
import com.sleeplessdog.pimi.games.domain.models.GroupUiDictionary
import com.sleeplessdog.pimi.utils.BlackPrimary
import com.sleeplessdog.pimi.utils.DarkTextDefault
import com.sleeplessdog.pimi.utils.Gray05
import com.sleeplessdog.pimi.utils.t3Text
import com.sleeplessdog.pimi.utils.t4Text

@Composable
fun UserGroupsTable(
    groups: List<GroupUiDictionary>,
    onNavigateToUserGroup: (String, String) -> Unit,
    onRenameGroup: (String, String) -> Unit,
    onDeleteGroup: (String, String) -> Unit,
    onPlayGroup: (String) -> Unit,
) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(scrollState)
            .padding(
                start = 20.dp,
                end = 20.dp,
                top = 16.dp,
            )
            .clip(RoundedCornerShape(12.dp))
    ) {

        Divider(color = BlackPrimary, thickness = 1.dp)

        groups.forEachIndexed { index, group ->
            val displayTitle = if (group.titleRes != 0) {
                stringResource(group.titleRes)
            } else {
                group.title
            }
            UserGroupTableRow(
                title = displayTitle,
                iconKey = group.iconRes,
                wordsCount = group.wordsInGroup,
                rowIndex = index,
                onClick = { onNavigateToUserGroup(group.key, group.title) },
                onRenameClick = { onRenameGroup(group.key, group.title) },
                onDeleteClick = { onDeleteGroup(group.key, group.title) },
                onPlayClick = { onPlayGroup(group.key) })
            if (index != groups.lastIndex) {
                Divider(color = BlackPrimary, thickness = 1.dp)
            }
        }
    }
}

@Composable
fun UserGroupTableRow(
    rowIndex: Int,
    title: String,
    iconKey: Int,
    wordsCount: Int? = null,
    onClick: () -> Unit,
    onRenameClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onPlayClick: () -> Unit,
) {
    val isSavedWords = title == "saved_words"
    var menuExpanded by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(68.dp)
            .background(Gray05)
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(iconKey),
            tint = DarkTextDefault,
            contentDescription = "Icon for row $rowIndex",
            modifier = Modifier
                .size(36.dp)
                .padding(start = 12.dp),

            )
        Spacer(modifier = Modifier.width(8.dp))
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = t3Text,
                color = DarkTextDefault,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth()
            )

            wordsCount?.let {
                Text(
                    text = pluralStringResource(
                        R.plurals.words_count, wordsCount, wordsCount
                    ),
                    style = t4Text,
                    color = DarkTextDefault.copy(alpha = 0.6f),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
        if (rowIndex != -2) {
            Box {
                Icon(
                    painter = painterResource(R.drawable.icon_dots_three_outline_vertical),
                    tint = DarkTextDefault,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(end = 10.dp)
                        .size(24.dp)
                        .clickable { menuExpanded = true })

                GroupActionsMenu(
                    expanded = menuExpanded,
                    onDismiss = { menuExpanded = false },
                    onRename = if (!isSavedWords) onRenameClick else null,
                    onDelete = if (!isSavedWords) onDeleteClick else null,
                    onPlay = onPlayClick,
                )
            }
        }
    }
}
