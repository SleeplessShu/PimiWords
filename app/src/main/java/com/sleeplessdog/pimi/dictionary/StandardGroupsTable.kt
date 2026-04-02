package com.sleeplessdog.pimi.dictionary

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.sleeplessdog.matchthewords.R
import com.sleeplessdog.pimi.games.domain.models.GroupUiDictionary
import com.sleeplessdog.pimi.utils.BlackPrimary
import com.sleeplessdog.pimi.utils.DarkTextDefault
import com.sleeplessdog.pimi.utils.Gray05
import com.sleeplessdog.pimi.utils.t3Text
import com.sleeplessdog.pimi.utils.t4Text

@Composable
fun StandardGroupsTable(
    groups: List<GroupUiDictionary>,
    onNavigateToGlobalGroup: (String, String) -> Unit,
    onPlayGroup: (String) -> Unit,
) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = 20.dp,
                end = 20.dp,
                top = 16.dp,
            )
            .verticalScroll(scrollState)
            .clip(RoundedCornerShape(12.dp))
    ) {
        groups.forEachIndexed { index, group ->

            val displayTitle = if (group.titleRes != 0) {
                stringResource(group.titleRes)
            } else {
                group.title
            }
            StandardGroupTableRow(
                wordsCount = group.wordsInGroup,
                iconKey = group.iconRes,
                title = displayTitle,
                groupKey = group.key,
                onClick = { onNavigateToGlobalGroup(group.key, group.title) },
                onPlayClick = { onPlayGroup(group.key) })

            if (index != groups.lastIndex) {
                Divider(color = BlackPrimary, thickness = 1.dp)
            }
        }
    }
}

@Composable
fun StandardGroupTableRow(
    wordsCount: Int,
    iconKey: Int,
    title: String,
    groupKey: String,
    onClick: () -> Unit,
    onPlayClick: () -> Unit,
) {
    val iconPainter = painterResource(id = iconKey)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Gray05)
            .height(68.dp)
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painter = iconPainter,
            contentDescription = null,
            tint = DarkTextDefault,
            modifier = Modifier
                .size(36.dp)
                .padding(start = 12.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                style = t3Text,
                color = DarkTextDefault,
                text = title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = pluralStringResource(
                    R.plurals.words_count, wordsCount, wordsCount
                ),
                style = t4Text,
                color = DarkTextDefault.copy(alpha = 0.6f),
            )
        }
        Icon(
            painter = painterResource(id = R.drawable.icon_play_circle),
            tint = DarkTextDefault,
            contentDescription = "Кнопка действия",
            modifier = Modifier
                .size(28.dp)
                .clickable {
                    onPlayClick()
                })
        Spacer(modifier = Modifier.padding(end = 12.dp))
    }
}
