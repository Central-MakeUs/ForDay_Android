package com.forday.app.core.designsystem.component.dropdown

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dayn.forday.R
import com.forday.app.core.designsystem.component.clickable.rememberThrottledClick
import com.forday.app.core.designsystem.theme.ForDayTheme

/**
 * SelectField - 드롭다운 선택 필드 컴포넌트
 *
 * @param placeholder 플레이스홀더 텍스트
 * @param selectedValue 현재 선택된 값
 * @param state 필드 상태 (Inactive, Active, Disabled)
 * @param onValueChange 값 변경 콜백
 * @param options 선택 가능한 옵션 리스트 (드롭다운 메뉴용)
 * @param modifier Modifier
 */
@Composable
fun SelectField(
    placeholder: String = "선택 사항을 입력해주세요.",
    selectedValue: String? = null,
    state: SelectFieldState = SelectFieldState.Inactive,
    onValueChange: ((String) -> Unit)? = null,
    options: List<String> = emptyList(),
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    val backgroundColor = when (state) {
        SelectFieldState.Inactive -> ForDayTheme.color.MediumGray
        SelectFieldState.Active -> ForDayTheme.color.MediumGray
        SelectFieldState.Disabled -> ForDayTheme.color.Gray03
    }

    val textColor = when (state) {
        SelectFieldState.Inactive -> ForDayTheme.color.Gray500
        SelectFieldState.Active -> ForDayTheme.color.Gray800
        SelectFieldState.Disabled -> ForDayTheme.color.StrongDivider
    }

    val iconTint = when (state) {
        SelectFieldState.Inactive -> ForDayTheme.color.Neutral600
        SelectFieldState.Active -> ForDayTheme.color.Neutral600
        SelectFieldState.Disabled -> ForDayTheme.color.StrongDivider
    }

    Box(modifier = modifier) {
        Row(
            modifier = Modifier
                .width(320.dp)
                .background(
                    color = backgroundColor,
                    shape = RoundedCornerShape(12.dp)
                )
                .clickable(enabled = state != SelectFieldState.Disabled) {
                    if (options.isNotEmpty()) {
                        expanded = !expanded
                    }
                }
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = selectedValue ?: placeholder,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                letterSpacing = (-0.42).sp,
                color = textColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Icon(
                painter = painterResource(id = R.drawable.ic_v),
                contentDescription = if (expanded) "Close dropdown" else "Open dropdown",
                tint = iconTint,
                modifier = Modifier
                    .size(16.dp)
                    .graphicsLayer {
                        rotationZ = if (expanded) 180f else 0f
                    }
            )
        }

        if (options.isNotEmpty()) {
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.width(320.dp)
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = option,
                                fontSize = 14.sp,
                                lineHeight = 20.sp,
                                letterSpacing = (-0.42).sp
                            )
                        },
                        onClick = rememberThrottledClick {
                            onValueChange?.invoke(option)
                            expanded = false
                        },
                    )
                }
            }
        }
    }
}

/**
 * SelectField 상태
 */
enum class SelectFieldState {
    Inactive,  // 기본 비활성 상태
    Active,    // 선택된 상태
    Disabled   // 비활성화 상태
}


@Composable
fun SelectFieldExample() {
    var selectedValue by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Inactive 상태 (선택 전)
        SelectField(
            placeholder = "선택 사항을 입력해주세요.",
            selectedValue = null,
            state = SelectFieldState.Inactive,
            options = listOf("옵션 1", "옵션 2", "옵션 3"),
            onValueChange = { selectedValue = it }
        )

        // Active 상태 (선택 후)
        SelectField(
            placeholder = "선택 사항을 입력해주세요.",
            selectedValue = selectedValue,
            state = if (selectedValue != null) SelectFieldState.Active else SelectFieldState.Inactive,
            options = listOf("옵션 1", "옵션 2", "옵션 3"),
            onValueChange = { selectedValue = it }
        )

        // Disabled 상태
        SelectField(
            placeholder = "선택 사항을 입력해주세요.",
            selectedValue = null,
            state = SelectFieldState.Disabled
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SelectFieldPreview() {
    SelectFieldExample()
}
