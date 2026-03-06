package com.forday.app.core.designsystem.component.button

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dayn.forday.R
import com.forday.app.core.designsystem.theme.ForDayTheme

@Composable
fun AIRecommendationButton(
    onCreateAiRoutines: () -> Unit,
    enabled: Boolean = true,  //  추가
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .shadow(
                elevation = if (enabled) 10.dp else 0.dp,  //  비활성화 시 그림자 제거
                shape = RoundedCornerShape(40.dp),
                spotColor = Color.Black.copy(alpha = 0.12f)
            ),
        shape = RoundedCornerShape(40.dp),
        color = if (enabled) ForDayTheme.color.White else Color(0xFFF6F6F6),  //  배경색 변경
        border = BorderStroke(
            1.dp,
            if (enabled) ForDayTheme.color.Primary001 else ForDayTheme.color.Gray03  //  테두리 색 변경
        )
    ) {
        Row(
            modifier = Modifier
                .clickable(
                    enabled = enabled,  // ✅ 클릭 제어
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    onCreateAiRoutines()
                }
                .padding(horizontal = 16.dp, vertical = 9.dp)
                .alpha(if (enabled) 1f else 0.5f),  // ✅ 투명도 조절
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // AI Icon
            Box(
                modifier = Modifier.size(22.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_ai),
                    contentDescription = "AI",
                    tint = Color.Unspecified,
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Text
            Text(
                text = "포데이 AI 추천 활동 보기",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = if (enabled) ForDayTheme.color.Gray800 else ForDayTheme.color.Gray500,  // ✅ 텍스트 색 변경
                lineHeight = (14 * 1.4).sp
            )

            // Arrow Icon
            Box(
                modifier = Modifier.size(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.icon_chevron_right),
                    contentDescription = "Arrow",
                    tint = if (enabled) ForDayTheme.color.Neutral900 else ForDayTheme.color.Gray500  // ✅ 아이콘 색 변경
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AIRecommendationButtonPreview() {
    ForDayTheme {
        AIRecommendationButton(onCreateAiRoutines = {})
    }
}