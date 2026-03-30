package com.sleeplessdog.pimi.dictionary.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.sleeplessdog.matchthewords.R
import com.sleeplessdog.pimi.dictionary.group_screen.WordUi
import com.sleeplessdog.pimi.dictionary.models.DialogType
import com.sleeplessdog.pimi.dictionary.models.UserGroupShort
import com.sleeplessdog.pimi.utils.DarkTextDefault
import com.sleeplessdog.pimi.utils.Gray05
import com.sleeplessdog.pimi.utils.Gray07
import com.sleeplessdog.pimi.utils.GreenPrimary
import com.sleeplessdog.pimi.utils.textSize14SemiBold
import com.sleeplessdog.pimi.utils.textSize16Bold
import com.sleeplessdog.pimi.utils.textSize16SemiBold
import com.sleeplessdog.pimi.utils.textSize24Bold

@Composable
fun MovingDialog(
    word: WordUi,
    groups: List<UserGroupShort>,
    onDismiss: () -> Unit,
    onConfirm: (targetGroupId: String) -> Unit,
) {
    var selectedGroupId by remember { mutableStateOf<String?>(null) }
    val scrollState = rememberScrollState()

    Dialog(
        onDismissRequest = onDismiss, properties = DialogProperties(
            usePlatformDefaultWidth = false, decorFitsSystemWindows = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Gray05)
                    .padding(16.dp)
            ) {

                Text(
                    text = stringResource(R.string.words_move),
                    style = textSize24Bold,
                    color = DarkTextDefault,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(16.dp))

                // переносимое слово
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Gray07)
                        .padding(12.dp)
                ) {
                    Text(
                        text = word.word, style = textSize16Bold, color = DarkTextDefault
                    )

                    Spacer(Modifier.height(4.dp))

                    Text(
                        text = word.translation,
                        style = textSize14SemiBold,
                        color = DarkTextDefault.copy(alpha = 0.6f)
                    )
                }

                Spacer(Modifier.height(16.dp))

                Text(
                    text = stringResource(R.string.select_group),
                    style = textSize16SemiBold,
                    color = DarkTextDefault
                )

                Spacer(Modifier.height(8.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 260.dp)
                        .verticalScroll(scrollState)
                ) {

                    groups.forEach { group ->

                        val selected = selectedGroupId == group.groupKey

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    if (selected) GreenPrimary.copy(alpha = 0.15f)
                                    else Color.Transparent
                                )
                                .clickable {
                                    selectedGroupId = group.groupKey
                                }
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically) {

                            Icon(
                                painter = painterResource(R.drawable.icon_move),
                                contentDescription = null,
                                tint = DarkTextDefault,
                                modifier = Modifier.size(20.dp)
                            )

                            Spacer(Modifier.width(12.dp))

                            Text(
                                text = group.title,
                                style = textSize16SemiBold,
                                color = DarkTextDefault
                            )
                        }

                        Spacer(Modifier.height(4.dp))
                    }
                }

                Divider(
                    modifier = Modifier.padding(vertical = 16.dp), color = DarkTextDefault
                )

                DialogButtons(
                    onDismiss = onDismiss, onConfirm = {
                        selectedGroupId?.let(onConfirm)
                    }, dialogType = DialogType.MOVE_PAIR
                )
            }
        }
    }
}
