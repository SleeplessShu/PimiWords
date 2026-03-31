package com.sleeplessdog.pimi.score.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.sleeplessdog.matchthewords.R
import com.sleeplessdog.pimi.score.domain.models.AwardsCatalog
import com.sleeplessdog.pimi.score.models.AwardMeta
import com.sleeplessdog.pimi.utils.BlackPrimary
import com.sleeplessdog.pimi.utils.White
import com.sleeplessdog.pimi.utils.t1Title
import com.sleeplessdog.pimi.utils.t2Title
import com.sleeplessdog.pimi.utils.t4Text

@Composable
public fun AwardScreen(
    awards: List<AwardMeta>,
    navController: NavController,
) {
    Scaffold(containerColor = BlackPrimary) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(20.dp)
                .navigationBarsPadding()
        ) {
            AwardsHeader(navController)

            Spacer(Modifier.height(16.dp))

            AwardsGrid(awards)
        }
    }
}

@Composable
private fun AwardsHeader(
    navController: NavController? = null,
) {
    Row(
        modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
    ) {

        Icon(
            painter = painterResource(id = R.drawable.ic_nav_cancel),
            contentDescription = stringResource(R.string.exit_button),
            tint = White,
            modifier = Modifier
                .size(28.dp)
                .clickable {
                    if (navController != null) {
                        navController.popBackStack()
                    }
                })
        Text(text = stringResource(R.string.my_awards), style = t1Title, color = White)
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun AwardsGrid(awards: List<AwardMeta>) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(1),
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
                .fillMaxWidth(0.8f)
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
        Spacer(modifier = Modifier.height(14.dp))
        Text(
            text = stringResource(award.title),
            style = t2Title,
            color = White,
            textAlign = TextAlign.Center
        )
        Text(
            text = stringResource(award.description),
            style = t4Text,
            color = White,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(18.dp))
    }
}

@Preview(showSystemUi = true, device = "spec:width=411dp,height=891dp")
@Preview(showSystemUi = true, device = "spec:width=360dp,height=640dp,dpi=300")
@Composable
private fun AwardScreenPreview() {
    AwardScreen(
        awards = AwardsCatalog.all, navController = NavController(LocalContext.current)
    )
}