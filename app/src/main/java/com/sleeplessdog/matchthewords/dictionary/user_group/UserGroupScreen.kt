package com.sleeplessdog.matchthewords.dictionary.user_group

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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import com.sleeplessdog.matchthewords.ui.theme.Gray05
import com.sleeplessdog.matchthewords.ui.theme.textSize14SemiBold
import com.sleeplessdog.matchthewords.ui.theme.textSize16Bold
import com.sleeplessdog.matchthewords.ui.theme.textSize20Medium
import com.sleeplessdog.matchthewords.ui.theme.textSize24Medium

@Composable
fun UserGroupUi(
    groupKey: String,
    viewModelWords: UserGroupViewModel,
    onBackClick: () -> Unit
) {

    val stateWords by viewModelWords.wordsState.collectAsState()

    LaunchedEffect(groupKey) {
        viewModelWords.loadWords(groupKey)
    }
    UserGroupScreen(
        onClick = onBackClick,
        words = stateWords,
    )
}

@Composable
fun UserGroupScreen(
    onClick: () -> Unit,
    words: List<WordWithTranslation>,
) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .background(BlackPrimary)
            .verticalScroll(scrollState)
    ) {
        HeaderMyGroup(onClick = onClick)
        Spacer(modifier = Modifier.height(8.dp))
        AddWord(onClick = onClick)
        Spacer(modifier = Modifier.height(20.dp))
        RowNumberWords()
        Spacer(modifier = Modifier.height(12.dp))
        WordAndTranslationTable(
            words = words
        )
    }
}

@Composable
fun HeaderMyGroup(onClick: () -> Unit) {
    val context = LocalContext.current
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.icon_back),
                contentDescription = "Левая иконка",
                modifier = Modifier
                    .padding(start = 16.dp)
                    .clickable { onClick() }
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "Название группы",
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
            contentDescription = "Иконка добавить",
            modifier = Modifier
                .size(36.dp)
                .padding(start = 12.dp),

            )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(
                "Добавить слово",
                style = textSize16Bold,
                color = DarkTextDefault
            )
        }
    }
}

@Composable
fun RowNumberWords() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(44.dp)
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "Количество слов",
            style = textSize20Medium,
            color = DarkTextDefault
        )
        Icon(
            painter = painterResource(id = R.drawable.icon_dots_three_outline_vertical),
            tint = DarkTextDefault,
            contentDescription = "Close",
            modifier = Modifier
                .clickable { }
                .padding(end = 16.dp)
                .size(24.dp)
        )
    }
}

@Composable
fun WordAndTranslationTable(
    words: List<WordWithTranslation>
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .offset(y = (-6).dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        words.forEachIndexed { index, word ->
            word.word?.let {
                word.translation?.let { it1 ->
                    WordAndTranslationTableRow(
                        titleKey = it,
                        translation = it1,
                    )
                }
            }
        }
    }
}

@Composable
fun WordAndTranslationTableRow(
    translation: String,
    titleKey: String,
) {
    val context = LocalContext.current
    val resId = remember(titleKey) {
        context.resources.getIdentifier(titleKey, "string", context.packageName)
    }
    val titleText = if (resId != 0) stringResource(id = resId) else titleKey

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Gray05)
            .height(68.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = Modifier.width(12.dp)) // вместо иконки сделать отступ

        Column(modifier = Modifier.weight(1f)) {
            Text(
                style = textSize16Bold,
                color = DarkTextDefault,
                text = titleText,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = translation,               // показываем перевод вместо количества слов
                style = textSize14SemiBold,
                color = DarkTextDefault.copy(alpha = 0.6f),
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Icon(
            painter = painterResource(id = R.drawable.icon_dots_three_outline_vertical),
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
fun MyGroupScreenPreview() {
    UserGroupScreen(
        onClick = {},
        words = listOf(
            WordWithTranslation(word = "Привет", translation = "Hello"),
            WordWithTranslation(word = "Кошка", translation = "Cat")
        )
    )
}