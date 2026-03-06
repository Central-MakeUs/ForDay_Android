package com.forday.app.core.designsystem.toast

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dayn.forday.R

/**
 * Toast Popup 컴포넌트
 *
 * 3가지 타입 지원:
 * 1. Toast_Success with Action: 아이콘 + 메시지 + 액션버튼
 * 2. Toast_Success: 아이콘 + 메시지만
 * 3. Toast_Common: 메시지만
 */
@Composable
fun ToastPopup(
    message: String,
    showIcon: Boolean = false,
    actionLabel: String? = null,
    onActionClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .width(320.dp)
            .height(44.dp),
        shape = RoundedCornerShape(12.dp),
        color = Color(0xAD000000) // rgba(0, 0, 0, 0.68)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = if (actionLabel != null) {
                Arrangement.SpaceBetween
            } else {
                Arrangement.Start
            },
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 왼쪽: 아이콘(옵션) + 메시지
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 성공 아이콘 (Toast_Success 타입일 때만)
                if (showIcon) {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .background(
                                color = Color(0xFFD9F7E5),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.icon_success),
                            contentDescription = "성공",
                            modifier = Modifier.size(12.dp),
                            tint = Color(0xFF2ECC71)
                        )
                    }
                }

                // 메시지 텍스트
                Text(
                    text = message,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 16.8.sp,
                    color = Color.White
                )
            }

            // 오른쪽: 액션 버튼 (옵션)
            if (actionLabel != null && onActionClick != null) {
                Text(
                    text = actionLabel,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal,
                    lineHeight = 16.8.sp,
                    color = Color(0xFFF2F2F2),
                    modifier = Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onActionClick
                    )
                )
            }
        }
    }
}

/**
 * Toast 상태 관리를 위한 데이터 클래스
 */
data class ToastState(
    val message: String,
    val showIcon: Boolean = false,
    val action: ToastAction? = null
)

data class ToastAction(
    val label: String,
    val onClick: () -> Unit
)

/**
 * Toast를 표시하는 컨테이너 Composable
 *
 * 애니메이션과 자동 숨김 기능 포함
 */
@Composable
fun ToastContainer(
    toastState: ToastState?,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = toastState != null,
        enter = slideInVertically(
            initialOffsetY = { it }
        ) + fadeIn(),
        exit = slideOutVertically(
            targetOffsetY = { it }
        ) + fadeOut(),
        modifier = modifier
    ) {
        toastState?.let { state ->
            ToastPopup(
                message = state.message,
                showIcon = state.showIcon,
                actionLabel = state.action?.label,
                onActionClick = state.action?.onClick
            )
        }
    }

    // 3초 후 자동 숨김
    LaunchedEffect(toastState) {
        if (toastState != null) {
            kotlinx.coroutines.delay(2000)
            onDismiss()
        }
    }
}

@Preview
@Composable
fun ToastPopupPreview() {
}