package com.forday.app.core.designsystem.component.checkbox

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.forday.app.core.designsystem.theme.ForDayTheme
import com.dayn.forday.R

/**
 * ForDay 체크박스 (사각형)
 *
 * @param checked 체크 상태
 * @param onCheckedChange 체크 상태 변경 이벤트
 * @param modifier Modifier
 * @param enabled 활성화 여부
 */
@Composable
fun ForDayCheckboxSquare(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Box(
        modifier = modifier
            .size(20.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(
                color = if (checked) {
                    ForDayTheme.color.Orange01  // Action/001
                } else {
                    ForDayTheme.color.White
                }
            )
            .border(
                width = if (checked) 0.dp else 1.dp,
                color = ForDayTheme.color.Border,  // Stroke/002
                shape = RoundedCornerShape(4.dp)
            )
            .clickable(enabled = enabled) { onCheckedChange(!checked) },
        contentAlignment = Alignment.Center
    ) {
        if (checked) {
            Icon(
                painter = painterResource(id = R.drawable.ic_check),
                contentDescription = null,
                modifier = Modifier.size(12.dp),
                tint = ForDayTheme.color.White
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ForDayCheckSquarePreview() {
    ForDayTheme {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ForDayCheckboxSquare(checked = false, onCheckedChange = {})
            ForDayCheckboxSquare(checked = true, onCheckedChange = {})
        }
    }
}