package com.aripuca.finhelper.ui.screens.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aripuca.finhelper.R
import com.aripuca.finhelper.ui.components.layout.VerticalSpacer

data class HistoryPanelState(
    val selectedHistoryItemIndex: Int = 0,
    val historyItemCount: Int = 0,
    val saveHistoryItemEnabled: Boolean = false,
    val onAddHistoryItem: () -> Unit = {},
    val onSaveHistoryItem: () -> Unit = {},
    val onDeleteHistoryItem: () -> Unit = {},
    val onLoadPrevHistoryItem: () -> Unit = {},
    val onLoadNextHistoryItem: () -> Unit = {},
)

@Preview
@Composable
fun HistoryPanel(
    selectedHistoryItemIndex: Int = 0,
    historyItemCount: Int = 0,
    saveEnabled: Boolean = false,
    onAddClick: () -> Unit = {},
    onSaveClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {},
    onPrevClick: () -> Unit = {},
    onNextClick: () -> Unit = {}
) {

    VerticalSpacer(14.dp)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .border(
                border = BorderStroke(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outline
                ),
                shape = RoundedCornerShape(4.dp)
            )
            .padding(horizontal = 16.dp, vertical = 0.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {

        Text(text = "History")

        if (historyItemCount > 0) {

            Spacer(modifier = Modifier.weight(1.0f))

            IconButton(
                onClick = { onPrevClick() },
                enabled = historyItemCount > 1
            ) {
                Icon(
                    painter = painterResource(
                        id = R.drawable.ic_left,
                    ),
                    contentDescription = stringResource(R.string.load_previous_history_item),
                    modifier = Modifier
                        .padding(8.dp)
                )
            }

            Text(text = "${selectedHistoryItemIndex + 1}/${historyItemCount}")

            IconButton(
                onClick = { onNextClick() },
                enabled = historyItemCount > 1
            ) {
                Icon(
                    painter = painterResource(
                        id = R.drawable.ic_right,
                    ),
                    contentDescription = stringResource(R.string.load_next_history_item),
                    modifier = Modifier
                        .padding(8.dp)
                )
            }
        }

        Spacer(modifier = Modifier.weight(1.0f))

        IconButton(
            onClick = { onAddClick() }
        ) {
            Icon(
                painter = painterResource(
                    id = R.drawable.ic_add,
                ),
                contentDescription = stringResource(R.string.add_history_item),
                modifier = Modifier
                    .padding(8.dp)
            )
        }

        IconButton(
            onClick = { onSaveClick() },
            enabled = saveEnabled && historyItemCount > 0,
        ) {
            Icon(
                painter = painterResource(
                    id = R.drawable.ic_save,
                ),
                contentDescription = stringResource(R.string.save_history_item),
                modifier = Modifier
                    .padding(8.dp)
            )
        }

        IconButton(
            onClick = { onDeleteClick() },
            enabled = historyItemCount > 0
        ) {
            Icon(
                painter = painterResource(
                    id = R.drawable.ic_delete,
                ),
                contentDescription = stringResource(R.string.save_history_item),
                modifier = Modifier
                    .padding(8.dp)
            )
        }

    }
}

@Preview(showBackground = true)
@Composable
fun HistoryPanelPreview() {
    HistoryPanel(
        selectedHistoryItemIndex = 3,
        historyItemCount = 5,
        saveEnabled = false,
    )
}

@Preview(showBackground = true)
@Composable
fun HistoryPanelPreview2() {
    HistoryPanel(
        selectedHistoryItemIndex = 0,
        historyItemCount = 0,
        saveEnabled = true,
    )
}
