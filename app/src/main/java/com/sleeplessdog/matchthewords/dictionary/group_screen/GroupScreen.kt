package com.sleeplessdog.matchthewords.dictionary.group_screen

import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.unit.dp
import com.sleeplessdog.matchthewords.R
import com.sleeplessdog.matchthewords.dictionary.DialogType
import com.sleeplessdog.matchthewords.dictionary.WordActionsMenu
import com.sleeplessdog.matchthewords.dictionary.dialogs.DeletingDialog
import com.sleeplessdog.matchthewords.dictionary.dialogs.MovingDialog
import com.sleeplessdog.matchthewords.dictionary.dialogs.WordsPairDialog
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
    val scrollState = rememberScrollState()

    var showAddWordDialog by remember { mutableStateOf(false) }
    var editingWord by remember { mutableStateOf<WordUi?>(null) }
    var deletingWord by remember { mutableStateOf<WordUi?>(null) }
    var movingWord by remember { mutableStateOf<WordUi?>(null) }


    /*var deleteWord by remember { mutableStateOf<WordUi?>(null) }
    var moveWord by remember { mutableStateOf<WordUi?>(null) }*/

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BlackPrimary)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            HeaderUserGroup(
                title = state.groupTitle, onClick = onBackClick
            )

            Spacer(Modifier.height(8.dp))

            //AddWord(onClick = onAddWordClick)

            Spacer(Modifier.height(20.dp))

            RowNumberWords(state.wordsCount)

            Spacer(Modifier.height(12.dp))

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
                        end = 20.dp,

                        bottom = 80.dp
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
                onDismiss = { editingWord = null },
                onConfirm = { origin, translate ->
                    onEditWord(
                        WordUi(
                            id = word.id,
                            word = origin,
                            translation = translate
                        )
                    )
                },
                dialogType = DialogType.EDIT_PAIR
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
                }
            )
        }

        deletingWord?.let { word ->
            DeletingDialog(
                title = word.word,
                onDismiss = { deletingWord = null },
                onConfirm = {
                    onDeleteWord(word)
                    deletingWord = null
                },
                dialogType = DialogType.DELETE_PAIR
            )
        }
    }
}

@Composable
fun HeaderUserGroup(
    title: String,
    onClick: () -> Unit,
) {
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically
        ) {

            Image(
                painter = painterResource(id = R.drawable.icon_back),
                contentDescription = null,
                modifier = Modifier
                    .padding(start = 16.dp)
                    .clickable { onClick() })

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = title, style = textSize24Medium, color = DarkTextDefault
            )

            Spacer(modifier = Modifier.weight(1f))

            Icon(
                painter = painterResource(id = R.drawable.icon_park_outline_search),
                contentDescription = null,
                tint = DarkTextDefault,
                modifier = Modifier
                    .padding(end = 16.dp)
                    .clickable {
                        Toast.makeText(context, "Search clicked", Toast.LENGTH_SHORT).show()
                    })
        }
    }
}


@Composable
fun RowNumberWords(count: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(44.dp)
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Absolute.Right
    ) {

        /*   Text(
               text = stringResource(R.string.words_count_title),
               style = textSize20Medium,
               color = DarkTextDefault
           )
   */
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

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .clip(RoundedCornerShape(12.dp))
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
    //val context = LocalContext.current

    Row(

        modifier = Modifier
            .fillMaxWidth()
            .background(Gray05)
            .height(68.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        var menuExpanded by remember { mutableStateOf(false) }
        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {

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


/*Icon(
            painter = painterResource(id = R.drawable.icon_play_circle),
            tint = DarkTextDefault,
            contentDescription = null,
            modifier = Modifier
                .size(24.dp)
                .clickable {
                    Toast.makeText(context, "Play clicked", Toast.LENGTH_SHORT).show()
                }
        )*/
/*@Composable
fun AddWord(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .height(68.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Gray05)
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(R.drawable.icon_add),
            tint = DarkTextDefault,
            contentDescription = null,
            modifier = Modifier
                .size(36.dp)
                .padding(start = 12.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = stringResource(R.string.add_word),
            style = textSize16Bold,
            color = DarkTextDefault
        )
    }
}*/