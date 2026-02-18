/*
package com.sleeplessdog.matchthewords.dictionary.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sleeplessdog.matchthewords.R
import com.sleeplessdog.matchthewords.ui.theme.Gray03
import com.sleeplessdog.matchthewords.ui.theme.GreenPrimary
import com.sleeplessdog.matchthewords.ui.theme.textSize16SemiBold


@Composable
fun ButtonsCancelAndSave(
    onDismiss: () -> Unit,
    onSave: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.End
    ) {

        TextButton(
            onClick = onDismiss
        ) {
            Text(
                text = stringResource(R.string.button_cancel),
                color = Gray03,
                style = textSize16SemiBold
            )
        }

        Spacer(Modifier.width(8.dp))

        TextButton(
            onClick = onSave
        ) {
            Text(
                text = stringResource(R.string.button_save),
                color = GreenPrimary,
                style = textSize16SemiBold
            )
        }
    }
}*/
