package com.forday.app.core.designsystem.component.toggle

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.forday.app.core.designsystem.theme.ForDayTheme

/**
 * FolderItemToggle - 폴더 항목을 추가/제거할 수 있는 토글 컴포넌트
 *
 * @param title 폴더 타이틀
 * @param isAdded 추가된 상태 여부
 * @param onToggle 토글 클릭 콜백
 * @param modifier Modifier
 */
@Composable
fun FolderItemToggle(
    title: String = "타이틀",
    isAdded: Boolean = false,
    onToggle: (Boolean) -> Unit = {},
    modifier: Modifier = Modifier
) {
    // 애니메이션 색상
    val backgroundColor by animateColorAsState(
        targetValue = if (isAdded) Color(0xFFFFF1E6) else ForDayTheme.color.White,
        label = "backgroundColor"
    )

    val iconBackgroundColor by animateColorAsState(
        targetValue = if (isAdded) ForDayTheme.color.White else ForDayTheme.color.White,
        label = "iconBackgroundColor"
    )

    val iconTint by animateColorAsState(
        targetValue = if (isAdded) Color(0xFFF0984E) else ForDayTheme.color.Neutral600,
        label = "iconTint"
    )

    val toggleBackgroundColor by animateColorAsState(
        targetValue = if (isAdded) Color(0xFFF0984E) else ForDayTheme.color.White,
        label = "toggleBackgroundColor"
    )

    val toggleBorderColor by animateColorAsState(
        targetValue = if (isAdded) Color(0xFFF0984E) else ForDayTheme.color.Border,
        label = "toggleBorderColor"
    )

    val toggleIconTint by animateColorAsState(
        targetValue = if (isAdded) ForDayTheme.color.White else ForDayTheme.color.Border,
        label = "toggleIconTint"
    )

    Row(
        modifier = modifier
            .width(320.dp)
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable { onToggle(!isAdded) }
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(21.35.dp)
                .background(
                    color = iconBackgroundColor,
                    shape = RoundedCornerShape(12.375.dp)
                )
                .padding(6.75.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_folder_up), // 실제 아이콘 리소스로 교체 필요
                contentDescription = "Folder",
                tint = iconTint,
                modifier = Modifier.size(7.594.dp)
            )
        }

        // 타이틀 텍스트
        Text(
            text = title,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            lineHeight = 19.6.sp, // 14 * 1.4
            color = Color(0xFF3A3A3A),
            modifier = Modifier.weight(1f)
        )

        // 토글 버튼
        ToggleButton(
            isChecked = isAdded,
            backgroundColor = toggleBackgroundColor,
            borderColor = toggleBorderColor,
            iconTint = toggleIconTint,
            onToggle = { onToggle(!isAdded) }
        )
    }
}

/**
 * 토글 버튼 컴포넌트
 */
@Composable
private fun ToggleButton(
    isChecked: Boolean,
    backgroundColor: Color,
    borderColor: Color,
    iconTint: Color,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(22.dp)
            .background(
                color = backgroundColor,
                shape = CircleShape
            )
            .clickable { onToggle() },
        contentAlignment = Alignment.Center
    ) {
        // 체크 아이콘
        Icon(
            painter = painterResource(id = R.drawable.ic_check_up), // 실제 아이콘 리소스로 교체 필요
            contentDescription = if (isChecked) "Added" else "Not added",
            tint = iconTint,
            modifier = Modifier.size(12.dp)
        )
    }
}


@Composable
fun FolderItemToggleExample() {
    var isAdded1 by remember { mutableStateOf(false) }
    var isAdded2 by remember { mutableStateOf(true) }
    var isAdded3 by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        FolderItemToggle(
            title = "타이틀",
            isAdded = isAdded1,
            onToggle = { isAdded1 = it }
        )

        FolderItemToggle(
            title = "음악 듣기",
            isAdded = isAdded2,
            onToggle = { isAdded2 = it }
        )

        FolderItemToggle(
            title = "운동하기",
            isAdded = isAdded3,
            onToggle = { isAdded3 = it }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun FolderItemTogglePreview() {
    ForDayTheme {
        FolderItemToggleExample()
    }
}

data class FolderItem(
    val title: String,
    val isAdded: Boolean
)
