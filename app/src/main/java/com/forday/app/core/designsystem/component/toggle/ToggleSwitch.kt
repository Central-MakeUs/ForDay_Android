package com.forday.app.core.designsystem.component.toggle

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.forday.app.core.designsystem.theme.ForDayTheme

/**
 * ForDay 토글 스위치
 *
 * @param checked 체크 상태
 * @param onCheckedChange 체크 상태 변경 이벤트
 * @param modifier Modifier
 * @param enabled 활성화 여부
 */
@Composable
fun ForDayToggleSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Box(
        modifier = modifier
            .width(37.dp)
            .height(23.dp)
            .clip(RoundedCornerShape(11.5.dp))
            .background(
                color = if (checked) {
                    ForDayTheme.color.Orange01  // Action/001
                } else {
                    ForDayTheme.color.Gray03  // Neutral/200
                }
            )
            .clickable(enabled = enabled) { onCheckedChange(!checked) }
    ) {
        // 토글 버튼
        Box(
            modifier = Modifier
                .size(15.dp)
                .align(if (checked) Alignment.CenterEnd else Alignment.CenterStart)
                .padding(horizontal = 3.dp)
                .background(
                    color = Color.White,
                    shape = CircleShape
                )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ForDayToggleSwitchPreview() {
    ForDayTheme {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ForDayToggleSwitch(checked = false, onCheckedChange = {})
            ForDayToggleSwitch(checked = true, onCheckedChange = {})
        }
    }
}