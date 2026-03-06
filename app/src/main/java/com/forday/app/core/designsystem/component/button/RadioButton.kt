package com.forday.app.core.designsystem.component.button

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.forday.app.core.designsystem.theme.ForDayTheme

/**
 * ForDay 라디오 버튼
 *
 * @param selected 선택 상태
 * @param onClick 클릭 이벤트
 * @param modifier Modifier
 * @param enabled 활성화 여부
 */
@Composable
fun ForDayRadioButton(
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Box(
        modifier = modifier
            .size(20.dp)
            .clip(CircleShape)
            .border(
                width = 1.dp,
                color = if (selected) {
                    ForDayTheme.color.Orange01
                } else {
                    ForDayTheme.color.Border
                },
                shape = CircleShape
            )
            .background(Color.White)
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (selected) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .background(
                        color = ForDayTheme.color.Orange01,
                        shape = CircleShape
                    )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ForDayRadioButtonPreview() {
    ForDayTheme {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ForDayRadioButton(selected = false, onClick = {})
            ForDayRadioButton(selected = true, onClick = {})
        }
    }
}