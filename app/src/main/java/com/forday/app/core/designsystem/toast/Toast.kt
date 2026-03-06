package com.forday.app.core.designsystem.toast

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
 * SuccessToast - 성공 메시지를 표시하는 토스트 컴포넌트
 *
 * @param message 표시할 메시지 텍스트
 * @param modifier Modifier
 */
@Composable
fun ErrorToast(
    message: String = "AI 취미활동 담기 완료!",
    iconVisible: Boolean = false,
    actionLabel: String? = null,
    onActionClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .padding(horizontal = 20.dp)
            .fillMaxWidth()
            .background(
                color = Color.Black.copy(alpha = 0.8f),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 20.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f, fill = false)
        ) {
            if (iconVisible) {
                // 체크 아이콘이 있는 원형 배경
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .background(
                            color = Color(0xFFEDFBF3),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_check),
                        contentDescription = "Success",
                        tint = Color(0xFF00C853),
                        modifier = Modifier.size(12.dp)
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

        if (actionLabel != null && onActionClick != null) {
            Text(
                text = actionLabel,
                fontSize = 12.sp,
                fontWeight = FontWeight.W400,
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

/**
 * 토스트를 표시하기 위한 확장 함수 (optional)
 * 실제 사용 시 SnackbarHost와 함께 사용하거나 별도의 토스트 시스템 구현 필요
 */
@Composable
fun rememberSuccessToastState(): SuccessToastState {
    return remember { SuccessToastState() }
}

class SuccessToastState {
    var isVisible by mutableStateOf(false)
        private set

    var message by mutableStateOf("")
        private set

    fun show(msg: String) {
        message = msg
        isVisible = true
    }

    fun hide() {
        isVisible = false
    }
}

/**
 * SuccessToastHost - 토스트를 화면에 표시하는 컴포넌트
 */
@Composable
fun SuccessToastHost(
    state: SuccessToastState,
    modifier: Modifier = Modifier
) {
    if (state.isVisible) {
        Box(
            modifier = modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            ErrorToast(message = state.message)
        }

        // 자동으로 숨기기 (3초 후)
        LaunchedEffect(state.isVisible) {
            kotlinx.coroutines.delay(3000)
            state.hide()
        }
    }
}

//@Composable
//fun ExampleScreen(context: Context) {
//    val toastState = rememberSuccessToastState()
//
//    Box(modifier = Modifier.fillMaxSize()) {
//        Column(
//            modifier = Modifier.fillMaxSize(),
//            verticalArrangement = Arrangement.Center,
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//            Button(onClick = {
//                toastState.show("AI 취미활동 담기 완료!")
//            }) {
//                Text("토스트 표시")
//            }
//        }
//
//        // 토스트를 상단에 표시
//        SuccessToastHost(
//            state = toastState,
//            modifier = Modifier
//                .align(Alignment.TopCenter)
//                .padding(top = 16.dp)
//        )
//    }
//}

@Preview(showBackground = true, backgroundColor = 0xFFF5F5F5)
@Composable
fun SuccessToastPreview() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        ErrorToast(message = "AI 취미활동 담기 완료!")
    }
}
