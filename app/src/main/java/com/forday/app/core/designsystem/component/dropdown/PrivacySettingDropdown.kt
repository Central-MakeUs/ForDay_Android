package com.forday.app.core.designsystem.component.dropdown

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class VisibilityOption(val label: String) {
    PUBLIC("전체공개"),
//    FRIEND("친구공개"),  //TODO 나중에 소식탭..? 생기면 그때 비활성화 풀기
    PRIVATE("나만보기")
}

@Composable
fun VisibilitySelector(
    selectedOption: VisibilityOption = VisibilityOption.PUBLIC,
    onOptionSelected: (VisibilityOption) -> Unit = {},
    modifier: Modifier = Modifier
) {
    // 색상 정의
    val bgColor = Color.White
    val selectedTextColor = Color(0xFF3A3A3A) // neutral/800
    val unselectedTextColor = Color(0xFF7A7A7A) // neutral/600

    Column(
        modifier = modifier
            .shadow(
                elevation = 10.dp,
                shape = RoundedCornerShape(12.dp),
                spotColor = Color.Black.copy(alpha = 0.08f)
            )
            .background(
                color = bgColor,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 24.dp, vertical = 8.dp)
    ) {
        VisibilityOption.entries.forEach { option ->
            Text(
                text = option.label,
                modifier = Modifier
                    .wrapContentWidth()
                    .clickable { onOptionSelected(option) }
                    .padding(vertical = 8.dp),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                lineHeight = 19.6.sp, // 14 * 1.4
                color = if (option == selectedOption) {
                    selectedTextColor
                } else {
                    unselectedTextColor
                }
            )
        }
    }
}

// 사용 예시
@Composable
fun VisibilitySelectorPreview() {
    var selectedOption by remember { mutableStateOf(VisibilityOption.PUBLIC) }

    VisibilitySelector(
        selectedOption = selectedOption,
        onOptionSelected = { selectedOption = it }
    )
}