package com.forday.app.core.designsystem.component.checkbox

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dayn.forday.R
import com.forday.app.core.designsystem.theme.ForDayTheme

/**
 * ForDay 체크 아이콘 (단순 체크 표시)
 *
 * @param checked 체크 상태
 * @param onClick 클릭 이벤트
 * @param modifier Modifier
 * @param enabled 활성화 여부
 */
@Composable
fun ForDayCheckIcon(
    checked: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Icon(
        painter = painterResource(id = R.drawable.ic_check),
        contentDescription = null,
        modifier = modifier
            .size(24.dp)
            .clickable(enabled = enabled, onClick = onClick),
        tint = if (checked) {
            ForDayTheme.color.Orange01  // Action/001
        } else {
            ForDayTheme.color.Gray03  // Neutral/200
        }
    )
}

@Preview(showBackground = true)
@Composable
fun ForDayCheckIconPreview() {
    ForDayTheme {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ForDayCheckIcon(checked = false, onClick = {})
            ForDayCheckIcon(checked = true, onClick = {})
        }
    }
}