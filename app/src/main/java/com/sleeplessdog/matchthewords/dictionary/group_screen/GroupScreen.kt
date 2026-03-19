package com.sleeplessdog.matchthewords.dictionary.group_screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.unit.dp
import com.sleeplessdog.matchthewords.R
import com.sleeplessdog.matchthewords.dictionary.WordActionsMenu
import com.sleeplessdog.matchthewords.dictionary.dialogs.DeletingDialog
import com.sleeplessdog.matchthewords.dictionary.dialogs.MovingDialog
import com.sleeplessdog.matchthewords.dictionary.dialogs.WordsPairDialog
import com.sleeplessdog.matchthewords.dictionary.models.DialogType
import com.sleeplessdog.matchthewords.ui.theme.BlackPrimary
import com.sleeplessdog.matchthewords.ui.theme.DarkTextDefault
import com.sleeplessdog.matchthewords.ui.theme.Gray05
import com.sleeplessdog.matchthewords.ui.theme.textSize14SemiBold
import com.sleeplessdog.matchthewords.ui.theme.textSize16Bold
import com.sleeplessdog.matchthewords.ui.theme.textSize16SemiBold
import com.sleeplessdog.matchthewords.ui.theme.textSize24Medium

@Composable
fun GroupUi(
    viewModel: GroupViewModel,
    onBackClick: () -> Unit,
) {
    val state by viewModel.state.collectAsState()

    GroupScreen(
        state = state,
        onBackClick = onBackClick,
        addNewUserWordsPair = viewModel::addNewUserWordsPair,
        onEditWord = viewModel::onEditWord,
        onMoveWord = viewModel::onMoveWord,
        onDeleteWord = viewModel::onDeleteWord,
        onSaveToSavedWords = viewModel::onSaveToSavedWords
    )
}

@Composable
fun GroupScreen(
    state: GroupScreenState,
    onBackClick: () -> Unit,
    addNewUserWordsPair: (String, String) -> Unit,
    onEditWord: (WordUi) -> Unit,
    onDeleteWord: (WordUi) -> Unit,
    onMoveWord: (WordUi, String) -> Unit,
    onSaveToSavedWords: (WordUi) -> Unit,
) {


    var showAddWordDialog by remember { mutableStateOf(false) }
    var editingWord by remember { mutableStateOf<WordUi?>(null) }
    var deletingWord by remember { mutableStateOf<WordUi?>(null) }
    var movingWord by remember { mutableStateOf<WordUi?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BlackPrimary)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            HeaderUserGroup(
                title = state.groupTitle, onClick = onBackClick
            )

            Spacer(Modifier.height(8.dp))

            RowNumberWords(state.wordsCount)

            WordAndTranslationTable(
                words = state.words,
                groupType = state.groupType,
                onEditWordClick = { editingWord = it },
                onMoveWordClick = { movingWord = it },
                onDeleteWordClick = { deletingWord = it },
                onSaveToSavedWordsClick = { onSaveToSavedWords(it) },
                expanded = true
            )
        }

        if (state.groupType == GroupType.USER) {
            FloatingActionButton(
                onClick = { showAddWordDialog = true },
                containerColor = DarkTextDefault,
                contentColor = BlackPrimary,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(
                        end = 16.dp,
                        bottom = 60.dp
                    )
            ) {
                Icon(
                    painter = painterResource(R.drawable.icon_add), contentDescription = "Add word"
                )
            }
        }

        if (showAddWordDialog) {
            WordsPairDialog(
                dialogType = DialogType.NEW_PAIR,
                onDismiss = { showAddWordDialog = false },
                onConfirm = { origin, translate ->
                    addNewUserWordsPair(origin, translate)
                    showAddWordDialog = false
                },

                )
        }

        editingWord?.let { word ->
            WordsPairDialog(
                onDismiss = { editingWord = null }, onConfirm = { origin, translate ->
                    onEditWord(
                        WordUi(
                            id = word.id, word = origin, translation = translate
                        )
                    )
                }, dialogType = DialogType.EDIT_PAIR, word = word
            )
        }

        movingWord?.let { word ->
            MovingDialog(
                word = word,
                groups = state.groups,
                onDismiss = { movingWord = null },
                onConfirm = { targetGroupId ->
                    onMoveWord(word, targetGroupId)
                    movingWord = null
                })
        }

        deletingWord?.let { word ->
            DeletingDialog(
                title = word.word, onDismiss = { deletingWord = null }, onConfirm = {
                    onDeleteWord(word)
                    deletingWord = null
                }, dialogType = DialogType.DELETE_PAIR
            )
        }
    }
}

@Composable
fun HeaderUserGroup(
    title: String,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
    ) {


        Image(
            painter = painterResource(id = R.drawable.icon_back),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 16.dp)
                .clickable { onClick() })

        Text(
            text = title,
            style = textSize24Medium,
            color = DarkTextDefault,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}


@Composable
fun RowNumberWords(count: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            //.height(44.dp)
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Absolute.Right
    ) {
        Text(
            text = pluralStringResource(
                R.plurals.words_count, count, count
            ), style = textSize16SemiBold, color = DarkTextDefault
        )
    }
}

@Composable
fun WordAndTranslationTable(
    words: List<WordUi>,
    groupType: GroupType,
    onEditWordClick: (WordUi) -> Unit,
    onMoveWordClick: (WordUi) -> Unit,
    onDeleteWordClick: (WordUi) -> Unit,
    onSaveToSavedWordsClick: (WordUi) -> Unit,
    expanded: Boolean,
) {
    val wordsToShow = if (expanded) words else words.take(3)
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = 20.dp, end = 20.dp, top = 16.dp,
                //bottom = 80.dp
            )
            .clip(RoundedCornerShape(12.dp))
            .verticalScroll(scrollState)
    ) {

        wordsToShow.forEachIndexed { index, word ->
            WordAndTranslationTableRow(
                word = word,
                title = word.word,
                translation = word.translation,
                groupType = groupType,
                onEditWordClick = onEditWordClick,
                onMoveWordClick = onMoveWordClick,
                onDeleteWordClick = onDeleteWordClick,
                onSaveToSavedWordsClick = onSaveToSavedWordsClick,
            )

            if (index != words.lastIndex) {
                Divider(color = BlackPrimary, thickness = 1.dp)
            }
        }
    }
}


@Composable
fun WordAndTranslationTableRow(
    word: WordUi,
    title: String,
    translation: String,
    groupType: GroupType,
    onEditWordClick: (WordUi) -> Unit,
    onMoveWordClick: (WordUi) -> Unit,
    onDeleteWordClick: (WordUi) -> Unit,
    onSaveToSavedWordsClick: (WordUi) -> Unit,
) {
    Row(

        modifier = Modifier
            .fillMaxWidth()
            .background(Gray05)
            .height(68.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        var menuExpanded by remember { mutableStateOf(false) }
        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 16.dp)
        ) {

            Text(
                text = title, style = textSize16Bold, color = DarkTextDefault
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = translation,
                style = textSize14SemiBold,
                color = DarkTextDefault.copy(alpha = 0.6f)
            )
        }
        Box {

            Icon(
                painter = painterResource(R.drawable.icon_dots_three_outline_vertical),
                tint = DarkTextDefault,
                contentDescription = null,
                modifier = Modifier
                    .size(24.dp)
                    .clickable { menuExpanded = true })

            WordActionsMenu(
                expanded = menuExpanded,
                groupType = groupType,
                onDismiss = { menuExpanded = false },
                onEditWordClick = { onEditWordClick(word) },
                onMoveWordClick = { onMoveWordClick(word) },
                onDeleteWordClick = { onDeleteWordClick(word) },
                onSaveToSavedWordsClick = { onSaveToSavedWordsClick(word) },
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
    }
}
