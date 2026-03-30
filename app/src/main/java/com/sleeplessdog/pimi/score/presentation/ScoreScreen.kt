package com.sleeplessdog.pimi.score.presentation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.sleeplessdog.matchthewords.R
import com.sleeplessdog.pimi.score.domain.models.AwardsCatalog
import com.sleeplessdog.pimi.score.models.AwardMeta
import com.sleeplessdog.pimi.score.presentation.models.ScoreUiState
import com.sleeplessdog.pimi.score.presentation.models.StatItem
import com.sleeplessdog.pimi.score.presentation.models.StatsPeriod
import com.sleeplessdog.pimi.settings.LanguageLevel
import com.sleeplessdog.pimi.utils.BlackPrimary
import com.sleeplessdog.pimi.utils.Gray02
import com.sleeplessdog.pimi.utils.GreenPrimary
import com.sleeplessdog.pimi.utils.White
import com.sleeplessdog.pimi.utils.h1Header
import com.sleeplessdog.pimi.utils.h2Header
import com.sleeplessdog.pimi.utils.t4Text
import com.sleeplessdog.pimi.utils.t4TextNumbers
import com.sleeplessdog.pimi.utils.t5Text
import kotlinx.coroutines.delay

@Composable
public fun ScoreScreen(
    state: ScoreUiState,
    navController: NavController,
) {
    var selectedPeriod by remember { mutableStateOf(StatsPeriod.WEEK) }

    val stats = when (selectedPeriod) {
        StatsPeriod.WEEK -> state.statsWeek
        StatsPeriod.ALL_TIME -> state.statsAllTime
    }

    Scaffold(containerColor = BlackPrimary) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .navigationBarsPadding()
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .align(Alignment.TopCenter)
            ) {

                HeaderSection(level = state.level.toString())

                Spacer(Modifier.height(24.dp))

                StatsPeriodToggle(
                    selected = selectedPeriod, onSelected = { selectedPeriod = it })

                Spacer(Modifier.height(16.dp))

                StatisticsList(stats = stats)

                Spacer(Modifier.height(32.dp))

                AwardsHeader(navController)

                Spacer(Modifier.height(16.dp))

                AwardsGrid(state.awards)
            }
        }
    }
}

/* ---------- HEADER ---------- */

@Composable
private fun HeaderSection(level: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically

    ) {
        Text(
            text = stringResource(R.string.h1_my_level), style = h1Header, color = White
        )
        Text(
            text = level, style = h1Header, color = White
        )
    }
}

/* ---------- STATS TOGGLE ---------- */

@Composable
private fun StatsPeriodToggle(
    selected: StatsPeriod,
    onSelected: (StatsPeriod) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = stringResource(R.string.h2_statistic), style = h2Header, color = White)

        Box(
            modifier = Modifier
                .height(40.dp)
                .width(160.dp)
                .background(Gray02, RoundedCornerShape(16.dp))
        ) {
            val offset by animateDpAsState(
                targetValue = if (selected == StatsPeriod.WEEK) 0.dp else 80.dp,
                animationSpec = spring(stiffness = Spring.StiffnessMediumLow)
            )

            Box(
                modifier = Modifier
                    .padding(2.dp)
                    .fillMaxHeight()
                    .width(76.dp)
                    .offset(x = offset)
                    .background(GreenPrimary, RoundedCornerShape(14.dp))
            )

            Row(modifier = Modifier.fillMaxSize()) {
                Box(
                    Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { onSelected(StatsPeriod.WEEK) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Неделя",
                        style = t5Text,
                        color = if (selected == StatsPeriod.WEEK) Gray02 else White
                    )
                }
                Box(
                    Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { onSelected(StatsPeriod.ALL_TIME) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Все время",
                        style = t5Text,
                        color = if (selected == StatsPeriod.ALL_TIME) Gray02 else White
                    )
                }
            }
        }
    }
}

@Composable
private fun StatisticsList(stats: StatItem) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

        StatRow(stats)

    }
}

@Composable
private fun StatRow(value: StatItem) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.animateContentSize() // Плавное изменение высоты всей секции
    ) {
        StatItemLine(stringResource(R.string.stats_words_learned), value.wordsLearned)
        StatItemLine(stringResource(R.string.stats_categories_learned), value.categoriesLearned)
        StatItemLine(stringResource(R.string.stats_games_played), value.gamesPlayed)
        StatItemLine(stringResource(R.string.stats_scores), value.scoresGained)
    }
}

@Composable
private fun StatItemLine(label: String, number: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, style = t4Text, color = White)
        AnimatedValue(value = number, style = t4TextNumbers)
    }
}

@Composable
private fun AnimatedValue(value: Int, style: TextStyle) {
    AnimatedContent(
        targetState = value, transitionSpec = {
            // Если новое число больше — выезжает сверху, если меньше — снизу
            if (targetState > initialState) {
                (slideInVertically { height -> height } + fadeIn()).togetherWith(slideOutVertically { height -> -height } + fadeOut())
            } else {
                (slideInVertically { height -> -height } + fadeIn()).togetherWith(slideOutVertically { height -> height } + fadeOut())
            }.using(
                SizeTransform(clip = false)
            )
        }, label = "statAnimation"
    ) { targetValue ->
        Text(text = targetValue.toString(), style = style, color = White)
    }
}

/* ---------- AWARDS ---------- */

@Composable
private fun AwardsHeader(navController: NavController) {
    Row(
        modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(text = stringResource(R.string.h2_awards), style = h2Header, color = White)
        Text(
            text = stringResource(R.string.awards_header),
            style = h2Header,
            color = GreenPrimary,
            modifier = Modifier.clickable { navController.navigate("awards") })
    }
}

@Composable
private fun AwardsGrid(awards: List<AwardMeta>) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(
            bottom = 100.dp, top = 8.dp, start = 0.dp, end = 0.dp
        )
    ) {
        items(awards.size) { n ->
            AwardCard(award = awards[n])
        }
    }
}

@Composable
private fun AwardCard(award: AwardMeta) {
    Column(
        modifier = Modifier.fillMaxWidth(0.5f), horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            val resId = if (award.isLocked) award.iconLocked else award.iconUnlocked
            println("DEBUG_TAG: Award: ${award.title}, Resource ID: $resId")
            val safeResId = if (resId != 0) resId else R.drawable.ic_group_abstract
            Image(
                painter = painterResource(
                    id = safeResId
                ),
                contentDescription = stringResource(award.title),
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            if (award.isLocked) {
                LockedAwardIcon(award)
            }
        }
    }
}

@Composable
fun LockedAwardIcon(award: AwardMeta) {
    var showDescription by remember { mutableStateOf(false) }

    Box(contentAlignment = Alignment.Center) {

        Icon(
            imageVector = Icons.Default.Lock,
            contentDescription = null,
            tint = GreenPrimary,
            modifier = Modifier
                .size(80.dp)
                .clickable {
                    showDescription = true
                })

        if (showDescription) {
            Text(
                text = stringResource(award.description),
                color = White,
                style = t4Text,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .align(Alignment.Center)
                    .background(Gray02, RoundedCornerShape(8.dp))
                    .padding(18.dp)
            )

            LaunchedEffect(Unit) {
                delay(2_000)
                showDescription = false
            }
        }
    }
}


/* ---------- PREVIEW ---------- */

@Preview(showSystemUi = true, device = "spec:width=411dp,height=891dp")
@Preview(showSystemUi = true, device = "spec:width=360dp,height=640dp,dpi=300")
@Composable
private fun ScoreScreenPreview() {
    val awards = AwardsCatalog.all
    val unlocked = awards.filter { !it.isLocked }
    val locked = awards.filter { it.isLocked }
    val awardsResult = buildList {
        addAll(unlocked.take(5))
        if (size < 5) {
            addAll(locked.take(5 - size))
        }
    }
    val navController = NavController(LocalContext.current)
    ScoreScreen(
        state = ScoreUiState(
            level = LanguageLevel.A1, statsWeek = StatItem(
                wordsLearned = 26,
                categoriesLearned = 456,
                gamesPlayed = 32,
                scoresGained = 234,
            ), statsAllTime = StatItem(
                wordsLearned = 26,
                categoriesLearned = 456,
                gamesPlayed = 32,
                scoresGained = 234,
            ), awards = awardsResult
        ), navController = navController
    )
}
