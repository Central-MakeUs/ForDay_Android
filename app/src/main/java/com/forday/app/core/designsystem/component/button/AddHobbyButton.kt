package com.forday.app.core.designsystem.component.button

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.forday.app.core.designsystem.component.clickable.NoRippleInteractionSource
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.forday.app.core.designsystem.component.clickable.rememberThrottledClick

// Color Definitions
object ForDayColors {
    val Primary003 = Color(0xFFFFF1E6) // Background color
    val Primary = Color(0xFFFF9447) // Text color
    val White = Color(0xFFFFFFFF)
}

/**
 * MainButton Component - Soft Status
 * Based on Figma design: CMC_18th_ForDay
 *
 * @param text Button text to display
 * @param modifier Modifier for the button
 * @param onClick Click handler for the button
 * @param enabled Whether the button is enabled
 */
@Composable
fun AddHobbyButton(
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    enabled: Boolean = true
) {
    Button(
        onClick = rememberThrottledClick(onClick = onClick),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 0.dp, vertical = 6.dp),
        enabled = enabled,
        interactionSource = remember { NoRippleInteractionSource() },
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = ForDayColors.Primary003,
            contentColor = ForDayColors.Primary,
            disabledContainerColor = ForDayColors.Primary003.copy(alpha = 0.5f),
            disabledContentColor = ForDayColors.Primary.copy(alpha = 0.5f)
        ),
        contentPadding = PaddingValues(horizontal = 40.dp, vertical = 11.5.dp),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 0.dp,
            pressedElevation = 0.dp,
            disabledElevation = 0.dp
        )
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = text.uppercase(),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 16.8.sp, // 1.2em = 14 * 1.2 = 16.8
                textAlign = TextAlign.Center,
                color = ForDayColors.Primary
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun MainButtonMPreview() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        AddHobbyButton(
            text = "취미활동 추가하기"
        )
    }
}