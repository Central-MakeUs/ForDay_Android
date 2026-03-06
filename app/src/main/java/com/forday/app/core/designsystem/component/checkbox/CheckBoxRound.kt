package com.forday.app.core.designsystem.component.checkbox

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.forday.app.core.designsystem.theme.ForDayTheme
import com.dayn.forday.R

/**
 * ForDay 체크박스 (라운드)
 *
 * @param checked 체크 상태
 * @param onCheckedChange 체크 상태 변경 이벤트
 * @param modifier Modifier
 * @param enabled 활성화 여부
 */
@Composable
fun ForDayCheckboxRound(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Box(
        modifier = modifier
            .size(20.dp)
            .clip(CircleShape)
            .background(
                color = if (checked) {
                    ForDayTheme.color.Orange01
                } else {
                    Color.White
                }
            )
            .border(
                width = if (checked) 0.dp else 1.dp,
                color = ForDayTheme.color.Border,
                shape = CircleShape
            )
            .clickable(enabled = enabled) { onCheckedChange(!checked) },
        contentAlignment = Alignment.Center
    ) {
        if (checked) {
            Icon(
                painter = painterResource(id = R.drawable.ic_check),
                contentDescription = null,
                modifier = Modifier.size(12.dp),
                tint = Color.White
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ForDayCheckRoundPreview() {
    ForDayTheme {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ForDayCheckboxRound(checked = false, onCheckedChange = {})
            ForDayCheckboxRound(checked = true, onCheckedChange = {})
        }
    }
}