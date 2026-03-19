package com.sleeplessdog.matchthewords.dictionary

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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.sleeplessdog.matchthewords.R
import com.sleeplessdog.matchthewords.backend.domain.models.GroupUiDictionary
import com.sleeplessdog.matchthewords.ui.theme.BlackPrimary
import com.sleeplessdog.matchthewords.ui.theme.DarkTextDefault
import com.sleeplessdog.matchthewords.ui.theme.Gray05
import com.sleeplessdog.matchthewords.ui.theme.textSize14SemiBold
import com.sleeplessdog.matchthewords.ui.theme.textSize16Bold

@Composable
fun UserGroupsTable(
    groups: List<GroupUiDictionary>,
    onNavigateToUserGroup: (String, String) -> Unit,
    onRenameGroup: (String, String) -> Unit,
    onDeleteGroup: (String, String) -> Unit,
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
                //bottom = 80.dp
            )
            .clip(RoundedCornerShape(12.dp))
    ) {

        Divider(color = BlackPrimary, thickness = 1.dp)

        groups.forEachIndexed { index, group ->
            UserGroupTableRow(
                title = group.title,
                //groupKey = group.key,
                iconKey = group.iconRes,
                wordsCount = group.wordsInGroup,
                rowIndex = index,
                onClick = { onNavigateToUserGroup(group.key, group.title) },
                onRenameClick = { onRenameGroup(group.key, group.title) },
                onDeleteClick = { onDeleteGroup(group.key, group.title) }
            )
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
    //groupKey: String,
    iconKey: Int,
    wordsCount: Int? = null,
    onClick: () -> Unit,
    onRenameClick: () -> Unit,
    onDeleteClick: () -> Unit,
) {
    val clickableIconPainter = painterResource(id = R.drawable.icon_dots_three_outline_vertical)
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
                style = textSize16Bold,
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
                    style = textSize14SemiBold,
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
                        .clickable { menuExpanded = true }
                )

                GroupActionsMenu(
                    expanded = menuExpanded,
                    onDismiss = { menuExpanded = false },
                    onRename = onRenameClick,
                    onDelete = onDeleteClick
                )
            }
        }
    }
}