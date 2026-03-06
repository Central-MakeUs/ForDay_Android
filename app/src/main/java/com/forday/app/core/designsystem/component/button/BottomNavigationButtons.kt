package com.forday.app.core.designsystem.component.button

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import com.forday.app.core.designsystem.component.clickable.NoRippleInteractionSource
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.forday.app.core.designsystem.component.clickable.rememberThrottledClick
import com.forday.app.core.designsystem.theme.ForDayTheme

/**
 * ForDay 앱의 이전/다음 네비게이션 버튼 컴포넌트
 *
 * @param onPreviousClick 이전 버튼 클릭 이벤트
 * @param onNextClick 다음 버튼 클릭 이벤트
 * @param nextEnabled 다음 버튼 활성화 여부
 * @param modifier Modifier
 * @param showBackgroundGradient 배경 그라디언트 표시 여부
 * @param previousText 이전 버튼 텍스트 (기본: "이전")
 * @param nextText 다음 버튼 텍스트 (기본: "다음")
 */
@Composable
fun BottomNavigationButtons(
    onPreviousClick: () -> Unit,
    onNextClick: () -> Unit,
    nextEnabled: Boolean = true,
    modifier: Modifier = Modifier,
    showBackgroundGradient: Boolean = true,
    previousText: String = "이전",
    nextText: String = "다음"
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(88.dp)
            .then(
                if (showBackgroundGradient) {
                    Modifier.background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0f),
                                Color.White
                            ),
                            startY = 0f,
                            endY = Float.POSITIVE_INFINITY
                        )
                    )
                } else {
                    Modifier
                }
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // 이전 버튼 (항상 비활성화 스타일)
            Button(
                onClick = rememberThrottledClick(onClick = onPreviousClick),
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                interactionSource = remember { NoRippleInteractionSource() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFE5E5E5),  // Action/003
                    contentColor = Color(0xFF7A7A7A)     // Neutral/600
                ),
                shape = RoundedCornerShape(12.dp),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 0.dp,
                    pressedElevation = 0.dp
                )
            ) {
                Text(
                    text = previousText,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 16.8.sp
                )
            }

            // 다음 버튼 (활성화 상태에 따라 변경)
            Button(
                onClick = rememberThrottledClick(onClick = onNextClick),
                enabled = nextEnabled,
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                interactionSource = remember { NoRippleInteractionSource() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (nextEnabled) {
                        Color(0xFFEE9449)  // Action/001
                    } else {
                        Color(0xFFFFE6D1)  // Primary/002 (Soft)
                    },
                    contentColor = Color.White,
                    disabledContainerColor = Color(0xFFFFE6D1),
                    disabledContentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 0.dp,
                    pressedElevation = 0.dp,
                    disabledElevation = 0.dp
                )
            ) {
                Text(
                    text = nextText,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 16.8.sp
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BottomNavigationButtonsEnabledPreview() {
    ForDayTheme {
        BottomNavigationButtons(
            onPreviousClick = {},
            onNextClick = {},
            nextEnabled = true
        )
    }
}

@Preview(showBackground = true)
@Composable
fun BottomNavigationButtonsDisabledPreview() {
    ForDayTheme {
        BottomNavigationButtons(
            onPreviousClick = {},
            onNextClick = {},
            nextEnabled = false
        )
    }
}