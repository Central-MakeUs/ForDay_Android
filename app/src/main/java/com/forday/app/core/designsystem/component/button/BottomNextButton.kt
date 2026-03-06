package com.forday.app.core.designsystem.component.button

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.Modifier
import com.forday.app.core.designsystem.component.clickable.NoRippleInteractionSource
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.forday.app.core.designsystem.theme.ForDayTheme

/**
 * 버튼 상태
 */
enum class BottomButtonState {
    ENABLED,    // 활성화 (주황색)
    DISABLED,   // 비활성화 (회색)
    SOFT        // 소프트 (연한 주황색)
}

/**
 * ForDay 앱의 하단 액션 버튼 컴포넌트
 *
 * @param text 버튼 텍스트
 * @param state 버튼 상태 (ENABLED, DISABLED, SOFT)
 * @param onClick 클릭 이벤트
 * @param modifier Modifier
 * @param showBackgroundGradient 배경 그라디언트 표시 여부
 */
@Composable
fun BottomNextButton(
    text: String,
    state: BottomButtonState = BottomButtonState.ENABLED,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    showBackgroundGradient: Boolean = true,
    backgroundColor: Color = Color.White
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(88.dp)
            .background(backgroundColor)
            .then(
                if (showBackgroundGradient) {
                    Modifier.background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                backgroundColor.copy(alpha = 0f),
                                backgroundColor
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
        Button(
            onClick = onClick,
            enabled = state == BottomButtonState.ENABLED || state == BottomButtonState.SOFT,
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .align(Alignment.BottomCenter),
            interactionSource = remember { NoRippleInteractionSource() },
            colors = ButtonDefaults.buttonColors(
                containerColor = when (state) {
                    BottomButtonState.ENABLED -> Color(0xFFEE9449)  // Action/001
                    BottomButtonState.DISABLED -> Color(0xFFE5E5E5)  // Action/003
                    BottomButtonState.SOFT -> Color(0xFFFFE6D1)      // Primary/002
                },
                disabledContainerColor = Color(0xFFE5E5E5)
            ),
            shape = RoundedCornerShape(12.dp),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 0.dp,
                pressedElevation = 0.dp,
                disabledElevation = 0.dp
            )
        ) {
            Text(
                text = text,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = when (state) {
                    BottomButtonState.ENABLED -> Color.White
                    BottomButtonState.DISABLED -> Color.White
                    BottomButtonState.SOFT -> Color(0xFFEE9449)  // Action/001 텍스트
                },
                lineHeight = 19.2.sp
            )
        }
    }
}

/**
 * enabled 상태로 간단하게 사용하는 버전
 */
@Composable
fun BottomNextButton(
    text: String,
    enabled: Boolean = true,
    onNext: () -> Unit = {},
    modifier: Modifier = Modifier,
    showBackgroundGradient: Boolean = true,
    backgroundColor: Color = Color.White
) {
    BottomNextButton(
        text = text,
        state = if (enabled) BottomButtonState.ENABLED else BottomButtonState.DISABLED,
        onClick = onNext,
        modifier = modifier,
        showBackgroundGradient = showBackgroundGradient,
        backgroundColor = backgroundColor
    )
}

@Preview(showBackground = true)
@Composable
fun BottomActionButtonEnabledPreview() {
    ForDayTheme {
        BottomNextButton(
            text = "시작하기",
            state = BottomButtonState.ENABLED,
            onClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun BottomActionButtonDisabledPreview() {
    ForDayTheme {
        BottomNextButton(
            text = "시작하기",
            state = BottomButtonState.DISABLED,
            onClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun BottomActionButtonSoftPreview() {
    ForDayTheme {
        BottomNextButton(
            text = "시작하기",
            state = BottomButtonState.SOFT,
            onClick = {}
        )
    }
}