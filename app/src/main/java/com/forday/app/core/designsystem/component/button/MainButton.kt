package com.forday.app.core.designsystem.component.button

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.forday.app.core.designsystem.theme.ForDayTheme
import java.util.Locale

enum class MainButtonStatus {
    ACTIVE,      // Gradient background, white text
    LINE,        // White background with orange border, orange text
    SOFT,        // Light orange background, orange text
    DISABLED     // Gray background, white text
}

@Composable
fun MainButton(
    text: String = "포데이 시작하기",
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    status: MainButtonStatus = MainButtonStatus.ACTIVE,
    showLeftIcon: Boolean = false,
    showRightIcon: Boolean = true,
    leftIcon: @Composable (() -> Unit)? = null,
    rightIcon: @Composable (() -> Unit)? = null,
    enabled: Boolean = status != MainButtonStatus.DISABLED
) {
    val backgroundColor = when (status) {
        MainButtonStatus.ACTIVE -> null // Gradient will be used
        MainButtonStatus.LINE -> ForDayTheme.color.White
        MainButtonStatus.SOFT -> ForDayTheme.color.Primary03
        MainButtonStatus.DISABLED -> ForDayTheme.color.Gray03
    }

    val textColor = when (status) {
        MainButtonStatus.ACTIVE -> ForDayTheme.color.White
        MainButtonStatus.LINE -> ForDayTheme.color.Primary001
        MainButtonStatus.SOFT -> ForDayTheme.color.Primary001
        MainButtonStatus.DISABLED -> ForDayTheme.color.White
    }

    val borderStroke = when (status) {
        MainButtonStatus.LINE -> BorderStroke(1.dp, ForDayTheme.color.Primary001)
        else -> null
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .then(
                    if (status == MainButtonStatus.ACTIVE) {
                        Modifier.background(
                            brush = ForDayTheme.gradients.gradient002
                        )
                    } else {
                        Modifier.background(backgroundColor ?: Color.Transparent)
                    }
                )
                .then(
                    if (borderStroke != null) {
                        Modifier.border(
                            width = borderStroke.width,
                            color = ForDayTheme.color.Primary001,
                            shape = RoundedCornerShape(12.dp)
                        )
                    } else {
                        Modifier
                    }
                )
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() },
                    enabled = enabled,
                    onClick = onClick
                )
                .padding(horizontal = 40.dp, vertical = 14.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (showLeftIcon) {
                    Box(
                        modifier = Modifier.size(20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (leftIcon != null) {
                            leftIcon()
                        } else {
                            DefaultIcon(tint = textColor)
                        }
                    }
                }

                Text(
                    text = text.replaceFirstChar {
                        if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
                    },
                    style = ForDayTheme.typography.title16,
                    color = textColor,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )

                if (showRightIcon) {
                    Box(
                        modifier = Modifier.size(20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (rightIcon != null) {
                            rightIcon()
                        } else {
                            DefaultIcon(tint = textColor)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DefaultIcon(
    tint: Color = Color.White
) {
    Box(
        modifier = Modifier
            .size(16.dp)
            .background(tint.copy(alpha = 0.3f), RoundedCornerShape(2.dp))
    )
}

@Preview
@Composable
fun MainButtonPreview() {
    ForDayTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFCCCCCC))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Active State (Gradient):", style = TextStyle(fontSize = 12.sp))
            MainButton(
                text = "포데이 시작하기",
                status = MainButtonStatus.ACTIVE,
                showLeftIcon = false,
                showRightIcon = true,
                onClick = { }
            )

            Text("Disabled State:", style = TextStyle(fontSize = 12.sp))
            MainButton(
                text = "포데이 시작하기",
                status = MainButtonStatus.DISABLED,
                showLeftIcon = false,
                showRightIcon = true,
                onClick = { }
            )

            Text("Line State (Outlined):", style = TextStyle(fontSize = 12.sp))
            MainButton(
                text = "포데이 시작하기",
                status = MainButtonStatus.LINE,
                showLeftIcon = false,
                showRightIcon = true,
                onClick = { }
            )

            Text("Soft State (Light background):", style = TextStyle(fontSize = 12.sp))
            MainButton(
                text = "포데이 시작하기",
                status = MainButtonStatus.SOFT,
                showLeftIcon = false,
                showRightIcon = true,
                onClick = { }
            )
        }
    }
}