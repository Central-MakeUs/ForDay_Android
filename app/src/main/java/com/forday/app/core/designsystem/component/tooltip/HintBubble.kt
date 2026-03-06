package com.forday.app.core.designsystem.component.tooltip

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.forday.app.core.designsystem.component.clickable.NoRippleInteractionSource
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dayn.forday.R

/**
 * HintBubble - 말풍선 형태의 힌트 컴포넌트
 *
 * @param text 표시할 메시지 텍스트
 * @param showIcon 닫기 아이콘 표시 여부
 * @param color 색상 테마 ("white" 또는 "black")
 * @param onDismiss 닫기 버튼 클릭 시 콜백 (optional)
 */
@Composable
fun HintBubble(
    text: String = "Shows a call-to-action message.",
    showIcon: Boolean = true,
    color: HintBubbleColor = HintBubbleColor.White,
    onDismiss: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val backgroundColor = when (color) {
        HintBubbleColor.White -> Color.White
        HintBubbleColor.Black -> Color(0xFF1E1E1E)
    }

    val textColor = when (color) {
        HintBubbleColor.White -> Color(0xFF1E1E1E)
        HintBubbleColor.Black -> Color.White
    }

    Column(
        modifier = modifier
            .wrapContentSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 메인 버블
        Row(
            modifier = Modifier
                .shadow(
                    elevation = 20.dp,
                    shape = RoundedCornerShape(21.dp),
                    spotColor = Color.Black.copy(alpha = 0.1f)
                )
                .background(
                    color = backgroundColor,
                    shape = RoundedCornerShape(21.dp)
                )
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = text,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                lineHeight = 16.sp,
                color = textColor,
                textAlign = TextAlign.Center
            )

            if (showIcon) {
                IconButton(
                    onClick = { onDismiss?.invoke() },
                    modifier = Modifier.size(16.dp),
                    interactionSource = remember { NoRippleInteractionSource() }
                ) {
                    Icon(
                        painter = painterResource(
                            id = when (color) {
                                HintBubbleColor.White -> R.drawable.ic_close_black
                                HintBubbleColor.Black -> R.drawable.ic_close_white
                            }
                        ),
                        contentDescription = "Close",
                        tint = textColor,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .size(width = 10.dp, height = 4.dp)
                .rotate(180f)
        ) {
            // SVG 삼각형을 그리거나 이미지로 대체
            // 여기서는 Canvas를 사용하여 삼각형을 그릴 수 있습니다
            Triangle(color = backgroundColor)
        }
    }
}

/**
 * 삼각형 꼬리 그리기
 */
@Composable
private fun Triangle(color: Color) {
    androidx.compose.foundation.Canvas(
        modifier = Modifier.fillMaxSize()
    ) {
        val path = androidx.compose.ui.graphics.Path().apply {
            moveTo(size.width / 2f, size.height)
            lineTo(0f, 0f)
            lineTo(size.width, 0f)
            close()
        }
        drawPath(
            path = path,
            color = color
        )
    }
}

/**
 * 힌트 버블 색상 테마
 */
enum class HintBubbleColor {
    White,
    Black
}

@Preview(showBackground = true)
@Composable
fun HintBubblePreview() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        HintBubble(
            text = "Shows a call-to-action message.",
            showIcon = true,
            color = HintBubbleColor.White,
            onDismiss = { }
        )

        HintBubble(
            text = "Shows a call-to-action message.",
            showIcon = true,
            color = HintBubbleColor.Black,
            onDismiss = { }
        )
    }
}
