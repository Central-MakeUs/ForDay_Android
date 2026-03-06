package com.forday.app.core.designsystem.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.forday.app.core.designsystem.component.clickable.NoRippleInteractionSource
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.forday.app.core.designsystem.theme.FordayColor

@Composable
fun FordayNextButton(
    text: String = "다음",
    onNext: () -> Unit,
    isSelected: Boolean) {
    Button(
        onClick = onNext,
        enabled = isSelected,
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),
        interactionSource = remember { NoRippleInteractionSource() },
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) FordayColor.Red01 else FordayColor.Neutral50,
            contentColor = Color.White
        )
    ) {
        Text(text, fontSize = 16.sp)
    }
}
