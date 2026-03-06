package com.forday.app.core.designsystem.component.button

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.forday.app.core.designsystem.theme.FordayColor

@Composable
fun FordayCheckButton(
    isSelected: Boolean
) {
    Box(
        modifier = Modifier
            .size(28.dp)
            .clip(CircleShape)
            .border(
                color = if (isSelected) FordayColor.Red01 else FordayColor.Neutral50,
                width = 1.dp,
                shape = CircleShape
            )
            .background(
                if (isSelected) FordayColor.Red01
                else FordayColor.White
            ),
        contentAlignment = Alignment.Center
    ) {
        if (isSelected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "선택됨",
                tint = FordayColor.White,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}