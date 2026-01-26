package com.sleeplessdog.matchthewords.dictionary

import android.widget.Toast
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sleeplessdog.matchthewords.R
import com.sleeplessdog.matchthewords.ui.theme.BlackPrimary
import com.sleeplessdog.matchthewords.ui.theme.DarkTextDefault
import com.sleeplessdog.matchthewords.ui.theme.Gray03
import com.sleeplessdog.matchthewords.ui.theme.Gray05
import com.sleeplessdog.matchthewords.ui.theme.textSize14SemiBold
import com.sleeplessdog.matchthewords.ui.theme.textSize16Bold
import com.sleeplessdog.matchthewords.ui.theme.textSize16SemiBold
import com.sleeplessdog.matchthewords.ui.theme.textSize20Medium
import com.sleeplessdog.matchthewords.ui.theme.textSize24Medium

@Composable
fun DictionaryUi(viewModel: DictionaryViewModel) {
    val state by viewModel.categoriesGrouped.collectAsState()
    DictionaryScreen(state.userGroups, state.defaultGroups, bufferWords = listOf())
}

@Composable
fun DictionaryScreen(
    userGroups: List<GroupUiDictionary>,
    standardGroups: List<GroupUiDictionary>,
    bufferWords: List<String>,
) {
    var groupState by remember { mutableStateOf(DictionaryWordGroups.BOTH_PARTIALLY) }
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .background(BlackPrimary)
            .verticalScroll(scrollState)
    ) {

        HeaderDictionary()

        Spacer(modifier = Modifier.height(8.dp))

        MyGroupsHeader(
            showAll = groupState == DictionaryWordGroups.BOTH_PARTIALLY || groupState == DictionaryWordGroups.MY_ONLY,
            onClick = {
                groupState = if (groupState == DictionaryWordGroups.MY_ONLY) {
                    DictionaryWordGroups.BOTH_PARTIALLY
                } else {
                    DictionaryWordGroups.MY_ONLY
                }
            }, groupState = groupState
        )
        Spacer(modifier = Modifier.height(10.dp))

        if (groupState == DictionaryWordGroups.BOTH_PARTIALLY || groupState == DictionaryWordGroups.MY_ONLY
        ) {
            UserGroupsTable(
                userGroups,
                expanded = groupState == DictionaryWordGroups.MY_ONLY,
                bufferWords = bufferWords,
            )
            Spacer(modifier = Modifier.height(24.dp))
        }

        StandardGroupsHeader(
            showAll = groupState == DictionaryWordGroups.BOTH_PARTIALLY || groupState == DictionaryWordGroups.STANDARD_ONLY,
            onClick = {
                groupState = if (groupState == DictionaryWordGroups.STANDARD_ONLY) {
                    DictionaryWordGroups.BOTH_PARTIALLY
                } else {
                    DictionaryWordGroups.STANDARD_ONLY
                }
            },
            groupState = groupState
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (groupState == DictionaryWordGroups.BOTH_PARTIALLY || groupState == DictionaryWordGroups.STANDARD_ONLY
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            StandardGroupsTable(
                standardGroups,
                expanded = groupState == DictionaryWordGroups.STANDARD_ONLY,
            )

            Spacer(modifier = Modifier.height(60.dp))
        }
    }
}

@Composable
fun HeaderDictionary() {
    val context = LocalContext.current
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.pimiss),
                contentDescription = "Левая иконка",
                modifier = Modifier
                    .padding(start = 16.dp)
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = stringResource(R.string.dictionary),
                style = textSize24Medium,
                color = DarkTextDefault,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                painter = painterResource(id = R.drawable.icon_park_outline_search),
                contentDescription = "Правая иконка",
                tint = DarkTextDefault,
                modifier = Modifier
                    .padding(end = 16.dp)
                    .clickable {
                        Toast.makeText(context, "Лупа нажата", Toast.LENGTH_SHORT)
                            .show()
                    }
            )
        }
    }
}

@Composable
fun MyGroupsHeader(
    onClick: () -> Unit,
    showAll: Boolean,
    groupState: DictionaryWordGroups,
) {
    val textColor =
        if (groupState == DictionaryWordGroups.MY_ONLY) DarkTextDefault else Gray03
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 18.dp, end = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Мои группы",
            style = textSize20Medium,
            color = DarkTextDefault
        )
        Spacer(modifier = Modifier.weight(1f))
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                painter = painterResource(
                    id = if (showAll) R.drawable.icon_visibility
                    else R.drawable.icon_hidden
                ),
                tint = if (showAll) DarkTextDefault else Gray03,
                modifier = Modifier
                    .size(24.dp)
                    .clickable { onClick() },
                contentDescription = "Иконка все"
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                style = textSize16SemiBold,
                color = textColor,
                text = "Все",
                modifier = Modifier
                    .clickable { onClick() }
            )
        }
    }
}

@Composable
fun UserGroupsTable(
    groups: List<GroupUiDictionary>,
    expanded: Boolean,
    bufferWords: List<String>,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
            .padding(horizontal = 20.dp)
            .clip(RoundedCornerShape(12.dp))
    ) {
        UserGroupTableRow(
            titleKey = stringResource(R.string.added_words),
            rowIndex = -1,
            wordsCount = bufferWords.size
        )
        Divider(color = BlackPrimary, thickness = 1.dp)
        val groupsToShow = if (expanded) groups else groups.take(2)
        groupsToShow.forEachIndexed { index, group ->
            UserGroupTableRow(
                titleKey = group.titleKey,
                wordsCount = group.wordsInGroup,
                rowIndex = index
            )
            if (index != groups.lastIndex) {
                Divider(color = BlackPrimary, thickness = 1.dp)
            }
        }
        Divider(color = BlackPrimary, thickness = 1.dp)
        UserGroupTableRow(
            titleKey = stringResource(R.string.create_group),
            rowIndex = -2,
        )
    }
}

@Composable
fun UserGroupTableRow(
    rowIndex: Int,
    titleKey: String,
    wordsCount: Int? = null,
) {
    val context = LocalContext.current
    val resId = remember(titleKey) {
        context.resources.getIdentifier(titleKey, "string", context.packageName)
    }

    val titleText = if (resId != 0) stringResource(id = resId) else titleKey

    val clickableIconPainter =
        painterResource(id = R.drawable.icon_dots_three_outline_vertical)
    val leftIconPainter = when (rowIndex) {
        -1 -> painterResource(R.drawable.icon_favorite)
        -2 -> painterResource(R.drawable.icon_add)
        else -> painterResource(R.drawable.icon_book)
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(68.dp)
            .background(Gray05),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = leftIconPainter,
            tint = DarkTextDefault,
            contentDescription = "Icon for row $rowIndex",
            modifier = Modifier
                .size(36.dp)
                .padding(start = 12.dp),

            )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(
                titleText,
                style = textSize16Bold,
                color = DarkTextDefault
            )

            wordsCount?.let {
                Text(
                    "$wordsCount ${pluralizeWord(wordsCount)}",
                    style = textSize14SemiBold,
                    color = DarkTextDefault.copy(alpha = 0.6f),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
        Spacer(modifier = Modifier.weight(1f))

        if (rowIndex != -1 && rowIndex != -2) {
            Icon(
                painter = clickableIconPainter,
                tint = DarkTextDefault,
                contentDescription = "Clickable icon for row $rowIndex",
                modifier = Modifier
                    .padding(end = 10.dp)
                    .size(24.dp)
                    .clickable {
                        Toast.makeText(context, "Три точки нажаты", Toast.LENGTH_SHORT)
                            .show()
                    }
            )
        }
    }
}

fun pluralizeWord(count: Int): String {
    val n = count % 100
    return if (n in 11..14) {
        "слов"
    } else {
        when (n % 10) {
            1 -> "слово"
            2, 3, 4 -> "слова"
            else -> "слов"
        }
    }
}

@Composable
fun StandardGroupsHeader(
    showAll: Boolean,
    onClick: () -> Unit,
    groupState: DictionaryWordGroups,
) {
    val textColor =
        if (groupState == DictionaryWordGroups.STANDARD_ONLY) DarkTextDefault else Gray03
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 18.dp, end = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Стандартные группы",
            style = textSize20Medium,
            color = DarkTextDefault
        )
        Spacer(modifier = Modifier.weight(1f))
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                painter = painterResource(
                    id = if (showAll) R.drawable.icon_visibility else R.drawable.icon_hidden
                ),
                tint = if (showAll) DarkTextDefault else Gray03,
                modifier = Modifier
                    .size(24.dp)
                    .clickable { onClick() },
                contentDescription = if (showAll) "Иконка все" else "Иконка скрыто"
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "Все",
                style = textSize16SemiBold,
                color = textColor,
                modifier = Modifier
                    .clickable { onClick() }
            )
        }
    }
}

@Composable
fun StandardGroupsTable(
    groups: List<GroupUiDictionary>,
    expanded: Boolean,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .offset(y = (-6).dp)
            .clip(RoundedCornerShape(12.dp))
    ) {
        val groupsToShow = if (expanded) groups else groups.take(3)
        groupsToShow.forEachIndexed { index, group ->
            StandardGroupTableRow(
                titleKey = group.titleKey,
                wordsCount = group.wordsInGroup,
                iconKey = group.iconKey
            )

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
    titleKey: String,
) {
    val context = LocalContext.current
    val resId = remember(titleKey) {
        context.resources.getIdentifier(titleKey, "string", context.packageName)
    }
    val titleText = if (resId != 0) stringResource(id = resId) else titleKey

    val iconPainter = if (iconKey != 0) {
        painterResource(id = iconKey)
    } else {
        painterResource(id = R.drawable.icon_add_standard_group)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Gray05)
            .height(68.dp),
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
                style = textSize16Bold,
                color = DarkTextDefault,
                text = titleText,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(4.dp)) // Отступ между двумя текстами
            Text(
                text = "$wordsCount ${pluralizeWord(wordsCount)}",
                style = textSize14SemiBold,
                color = DarkTextDefault.copy(alpha = 0.6f),
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        Icon(
            painter = painterResource(id = R.drawable.icon_play_circle),
            tint = DarkTextDefault,
            contentDescription = "Кнопка действия",
            modifier = Modifier
                .size(24.dp)
                .clickable {
                    Toast.makeText(context, "Плей нажат", Toast.LENGTH_SHORT)
                        .show()
                }
        )
        Spacer(modifier = Modifier.padding(end = 12.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun DictionaryScreenPreview() {
    DictionaryScreen(
        userGroups = listOf(
            GroupUiDictionary(
                key = "saved_words",
                titleKey = "added_words",
                iconKey = R.drawable.icon_favorite,
                wordsInGroup = 3
            ),
            GroupUiDictionary(
                key = "birds",
                titleKey = "birds",
                iconKey = R.drawable.icon_book,
                wordsInGroup = 5
            ),
            GroupUiDictionary(
                key = "fish",
                titleKey = "fish",
                iconKey = R.drawable.icon_book,
                wordsInGroup = 1
            )
        ),
        standardGroups = listOf(
            GroupUiDictionary(
                key = "travel",
                titleKey = "travel",
                iconKey = R.drawable.icon_book,
                wordsInGroup = 1
            ),
            GroupUiDictionary(
                key = "house",
                titleKey = "house",
                iconKey = R.drawable.icon_book,
                wordsInGroup = 2
            ),
            GroupUiDictionary(
                key = "work",
                titleKey = "work",
                iconKey = R.drawable.icon_book,
                wordsInGroup = 5
            ),
            GroupUiDictionary(
                key = "birds_std",
                titleKey = "birds",
                iconKey = R.drawable.icon_book,
                wordsInGroup = 1
            )
        ),
        bufferWords = listOf("apple", "dog", "cat")
    )
}
