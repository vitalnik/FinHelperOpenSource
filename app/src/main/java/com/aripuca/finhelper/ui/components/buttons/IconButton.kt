package com.aripuca.finhelper.ui.components.buttons

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aripuca.finhelper.R
import com.aripuca.finhelper.ui.components.layout.HorizontalSpacer
import com.aripuca.finhelper.ui.theme.FinHelperTheme

@Composable
fun IconButton(
    text: String,
    textColor: Color = Color.Unspecified,
    iconId: Int,
    onClick: () -> Unit = {}
) {
    Button(
        modifier = Modifier,
        shape = RoundedCornerShape(50),
        onClick = {
            onClick()
        }
    ) {

        Icon(
            painter = painterResource(id = iconId),
            contentDescription = "",
        )

        HorizontalSpacer(24.dp)

        Text(
            text = text,
            modifier = Modifier.padding(vertical = 12.dp),
            style = TextStyle(fontSize = 22.sp),
            color = textColor
        )

        HorizontalSpacer()
    }
}

@Preview(showBackground = true)
@Composable
fun IconButtonPreview() {
    FinHelperTheme {
        IconButton(text = "Button Title", iconId = R.drawable.ic_diamond)
    }
}