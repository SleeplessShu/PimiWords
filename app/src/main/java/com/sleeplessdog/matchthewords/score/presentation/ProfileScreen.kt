package com.sleeplessdog.matchthewords.score.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.sleeplessdog.matchthewords.score.models.Award
import com.sleeplessdog.matchthewords.ui.theme.Pink

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = viewModel(), // Предполагается наличие вью-модели для данных
) {
    // Используем Scaffold для корректной обработки отступов навигации
    Scaffold(
        backgroundColor = AppColors.Background, // Твой кастомный цвет
        bottomBar = { /* Твоя навигация здесь */ }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            HeaderSection(level = "~B2")

            Spacer(modifier = Modifier.height(24.dp))

            StatisticsHeader()

            Spacer(modifier = Modifier.height(16.dp))

            StatisticsList()

            Spacer(modifier = Modifier.height(32.dp))

            AwardsHeader()

            Spacer(modifier = Modifier.height(16.dp))

            AwardsRow()
        }
    }
}

@Composable
fun HeaderSection(level: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Мой уровень",
            style = AppTypography.Header, // Твой кастомный стиль
            color = Color.White
        )
        Text(
            text = level,
            style = AppTypography.LevelBadge,
            color = Color.White
        )
    }
}

@Composable
fun StatisticsHeader() {
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Неделя", "Всё время")

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Статистика",
            color = Color.White,
            style = AppTypography.SectionTitle
        )

        // TabRow в стиле скриншота
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = AppColors.Surface // Темно-серый фон табов
        ) {
            TabRow(
                selectedTabIndex = selectedTabIndex,
                backgroundColor = Color.Transparent,
                contentColor = Color.White,
                indicator = { tabPositions ->
                    // Белая подложка для выбранного таба
                    Box(
                        Modifier
                            .tabIndicatorOffset(tabPositions[selectedTabIndex])
                            .fillMaxHeight()
                            .padding(2.dp)
                            .background(Color.White, RoundedCornerShape(14.dp))
                    )
                },
                divider = {},
                modifier = Modifier
                    .width(200.dp)
                    .height(36.dp)
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = {
                            Text(
                                text = title,
                                style = AppTypography.TabText,
                                color = if (selectedTabIndex == index) Color.Black else Color.Gray
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun AwardsRow() {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        // Пример данных. В реальном коде будет список из ViewModel
        items(getMockAwards()) { award ->
            AwardCard(award = award)
        }
    }
}

@Composable
fun AwardCard(award: Award) {
    Column(
        modifier = Modifier.width(140.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Контейнер для SVG
        Box(
            modifier = Modifier
                .size(120.dp)
                .background(
                    color = if (award.isLocked) Color(0xFF333333) else Color(Pink), //
                    shape = RoundedCornerShape(16.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            // Здесь будет твой Image(painterResource...) для SVG
            if (award.isLocked) {
                Icon(Icons.Default.Lock, contentDescription = null, tint = Color.Gray)
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = award.title,
            style = AppTypography.AwardTitle,
            textAlign = TextAlign.Center,
            maxLines = 2
        )
        Text(
            text = award.description,
            style = AppTypography.AwardDesc,
            textAlign = TextAlign.Center,
            color = Color.Gray
        )
    }
}