package com.sleeplessdog.matchthewords.game.presentation.dictionary

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Divider
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sleeplessdog.matchthewords.R
import com.sleeplessdog.matchthewords.ui.theme.BlackPrimary
import com.sleeplessdog.matchthewords.ui.theme.DarkTextDefault
import com.sleeplessdog.matchthewords.ui.theme.Gray03
import com.sleeplessdog.matchthewords.ui.theme.White01
import com.sleeplessdog.matchthewords.ui.theme.textSize16Bold
import com.sleeplessdog.matchthewords.ui.theme.textSize20Medium
import com.sleeplessdog.matchthewords.ui.theme.textSize24Medium

@Composable
fun DictionaryScreen(listWord: List<String>, listStandardWord: List<String>) {
    Column(modifier = Modifier.background(BlackPrimary)) {
        HeaderDictionary()
        Spacer(modifier = Modifier.height(8.dp))
        MyGroupsHeader()
        Spacer(modifier = Modifier.height(10.dp))
        MyGroupsTable(listWord)
        Spacer(modifier = Modifier.height(24.dp))
        StandardGroupsHeader()
        Spacer(modifier = Modifier.height(8.dp))
        Spacer(modifier = Modifier.height(10.dp))
        StandardGroupsTable(listStandardWord)
    }
}

@Composable
fun HeaderDictionary() {
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
                        // Действие при клике
                        println("Иконка нажата!")
                    }
            )
        }
    }
}

@Composable
fun MyGroupsHeader() {
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
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(id = R.drawable.icon_visibility),
                tint = DarkTextDefault,
                contentDescription = "Иконка все"
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                style = textSize16Bold,
                color = Gray03,
                text = "Все",
            )
        }
    }
}

@Composable
fun MyGroupsTable(listWord: List<String>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ) {
        // Первая строка — заголовок "Добавленные слова"
        MyGroupTableRow(
            stringWord = "Добавленные слова",
            rowIndex = -1, // можно передать -1, чтобы отличать от обычных
        )
        Divider(color = Color.Gray, thickness = 1.dp)
        (0..<listWord.size).forEach {
            MyGroupTableRow(
                stringWord = listWord[it],
                rowIndex = it
            )
            if (it != -1 && it != -2) {
                Divider(color = Color.Gray, thickness = 1.dp)
            }
        }
        // Отдельная строка "Создать группу"
        MyGroupTableRow(
            stringWord = "Создать группу",
            rowIndex = -2, // отдельный индекс для отличия
        )
    }
}

@Composable
fun MyGroupTableRow(rowIndex: Int, stringWord: String) {
    val clickableIconPainter =
        painterResource(id = R.drawable.icon_dots_three_outline_vertical) // замени ресурс
    val leftIconPainter = when (rowIndex) {
        -1 -> painterResource(R.drawable.icon_favorite)    // первая строка
        -2 -> painterResource(R.drawable.icon_add)    // последняя строка
        else -> painterResource(R.drawable.icon_book)       // обычные строки
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = leftIconPainter,
            tint = DarkTextDefault,
            contentDescription = "Icon for row $rowIndex",
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            stringWord,
            style = textSize16Bold,
            color = DarkTextDefault
        )
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
                        // TODO: обработка клика
                    }
            )
        }
    }
}

@Composable
fun StandardGroupsHeader() {
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
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(id = R.drawable.icon_visibility),
                tint = DarkTextDefault,
                contentDescription = "Иконка все"
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "Все",
                style = textSize16Bold,
                color = Gray03,
            )
        }
    }
}

@Composable
fun StandardGroupsTable(listStandardWord: List<String>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        listStandardWord.forEach {
            StandardGroupTableRow(text = it)
            Divider()  // разделитель между строками
        }
    }
}

@Composable
fun StandardGroupTableRow(
    text: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = R.drawable.icon_add_standard_group),
            contentDescription = null,
            tint = DarkTextDefault,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            style = textSize16Bold,
            color = DarkTextDefault,
            text = text,
            modifier = Modifier.weight(1f)
        )
        IconButton(onClick = {
            // Обработка нажатия на правую иконку
            println("Кнопка для $text нажата")
        }) {
            Icon(
                painter = painterResource(id = R.drawable.icon_play_circle),
                tint = White01,
                contentDescription = "Кнопка действия"
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DictionaryScreenPreview() {
    DictionaryScreen(
        listWord = listOf("Птички", "Приветствия"),
        listStandardWord = listOf("Путешествия", "Дом", "Работа")
    )
}