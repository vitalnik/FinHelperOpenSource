package com.aripuca.finhelper.ui.components.input

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aripuca.finhelper.ui.theme.FinHelperTheme

@Composable
fun OutlinedScrollableTabRow(
    label: String = "Tabs label",
    selectedTabIndex: Int,
    tabsList: List<String>,
    onTabSelected: (Int) -> Unit = {}
) {

    Box {

        Box(
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp)
                .border(
                    border = BorderStroke(
                        1.dp,
                        color = MaterialTheme.colorScheme.outline
                    ), shape = RoundedCornerShape(16.dp)
                )
                .clip(
                    shape = RoundedCornerShape(16.dp)
                )
        ) {

            ScrollableTabRow(
                modifier = Modifier.padding(top = 12.dp, bottom = 12.dp),
                selectedTabIndex = selectedTabIndex,
                divider = {}, /* Disable the built-in divider */
                edgePadding = 24.dp,
                indicator = {},
            ) {

                tabsList.forEachIndexed { index, year ->
                    Tab(
                        selected = index == selectedTabIndex,
                        onClick = {
                            onTabSelected(
                                index
                            )
                        },
                        modifier = Modifier.clip(
                            shape = RoundedCornerShape(16.dp)
                        )
                    ) {
                        Surface(
                            border = BorderStroke(
                                1.dp, color = if (index == selectedTabIndex)
                                    MaterialTheme.colorScheme.outline
                                else MaterialTheme.colorScheme.surface
                            ),
                            shape = RoundedCornerShape(16.dp),
                        ) {
                            Text(
                                text = year,
                                modifier = Modifier.padding(
                                    horizontal = 12.dp,
                                    vertical = 6.dp
                                )
                            )
                        }
                    }
                }
            }
        }

        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .padding(start = 28.dp)
                .background(color = MaterialTheme.colorScheme.surface)
                .padding(horizontal = 4.dp)
        )

    }
}

@Preview(showBackground = true)
@Composable
private fun OutlinedScrollableTabRow_Preview() {
    FinHelperTheme {
        OutlinedScrollableTabRow(
            selectedTabIndex = 2,
            tabsList = listOf("Tab 1", "Tab 2", "Tab 3"),
            onTabSelected = {})
    }
}
