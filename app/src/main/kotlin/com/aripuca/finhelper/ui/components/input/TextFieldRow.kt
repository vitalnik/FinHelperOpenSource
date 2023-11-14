package com.aripuca.finhelper.ui.components.input

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aripuca.finhelper.ui.theme.FinHelperTheme

@Composable
fun RowScope.TextFieldRow(
    label: String,
    value: String,
    onValueChanged: (String) -> Unit,
    enabled: Boolean = true,
    testTag: String = ""
) {
    OutlinedTextField(
        value = value,
        onValueChange = {
            onValueChanged(it)
        },
        modifier = Modifier
            .weight(1f, true)
            .testTag(testTag),
        label = { Text(text = label) },
        enabled = enabled,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
        ),
        shape = RoundedCornerShape(16.dp),
        singleLine = true,
    )
}

@Preview
@Composable
private fun TextFieldRow_Preview() {
    FinHelperTheme {
        Row {
            TextFieldRow(
                label = "Label text",
                value = "input text value",
                onValueChanged = { },
            )
        }
    }
}
