package com.forday.app.core.designsystem.component.textfield

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.forday.app.core.designsystem.theme.ForDayTheme

enum class TextFieldType {
    INPUT,
    TEXTAREA
}

@Composable
fun RoutineTextField(
    modifier: Modifier = Modifier,
    state: TextFieldState = TextFieldState.INACTIVE,
    type: TextFieldType = TextFieldType.INPUT,
    showScroll: Boolean = true,
    showLetterCount: Boolean = true,
    text: String = "",
    onTextChange: (String) -> Unit = {},
    maxLength: Int = if (type == TextFieldType.INPUT) 30 else 100
) {
    val backgroundColor = when (state) {
        TextFieldState.INACTIVE, TextFieldState.ACTIVE -> ForDayTheme.color.MediumGray
        TextFieldState.DISABLED -> ForDayTheme.color.Gray03
    }

    val textColor = when (state) {
        TextFieldState.INACTIVE -> ForDayTheme.color.Gray500
        TextFieldState.ACTIVE -> ForDayTheme.color.Gray800
        TextFieldState.DISABLED -> ForDayTheme.color.StrongDivider
    }

    val placeholderText = when {
        state == TextFieldState.ACTIVE -> "바로 출입 가능합니다."
        else -> "최대 30자까지 자유롭게 수정 가능"
    }

    val contentText = text.ifEmpty { placeholderText }

    Box(
        modifier = modifier
            .width(320.dp)
            .then(
                if (type == TextFieldType.TEXTAREA) {
                    Modifier.height(120.dp)
                } else {
                    Modifier.wrapContentHeight()
                }
            )
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(16.dp)
    ) {
        when (type) {
            TextFieldType.INPUT -> {
                Text(
                    text = contentText,
                    style = ForDayTheme.typography.label14,
                    color = textColor,
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.CenterStart)
                )
            }

            TextFieldType.TEXTAREA -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = contentText,
                        style = ForDayTheme.typography.label14,
                        color = textColor,
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (showLetterCount) {
                        Text(
                            text = "${text.length}/${maxLength}",
                            style = ForDayTheme.typography.label10,
                            color = if (state == TextFieldState.DISABLED) {
                                ForDayTheme.color.StrongDivider
                            } else {
                                ForDayTheme.color.StrongDivider
                            },
                            textAlign = TextAlign.End,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                if (showScroll) {
                    Box(
                        modifier = Modifier
                            .width(2.dp)
                            .height(64.dp)
                            .background(
                                color = ForDayTheme.color.StrongDivider,
                                shape = RoundedCornerShape(4.dp)
                            )
                            .align(Alignment.CenterEnd)
                            .offset(x = 12.dp)
                    )
                }
            }
        }
    }
}


@Composable
fun CustomTextFieldPreview() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFCCCCCC))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        RoutineTextField(
            state = TextFieldState.INACTIVE,
            type = TextFieldType.INPUT
        )

        // Active Input
        RoutineTextField(
            state = TextFieldState.ACTIVE,
            type = TextFieldType.INPUT,
            text = "바로 출입 가능합니다."
        )

        RoutineTextField(
            state = TextFieldState.DISABLED,
            type = TextFieldType.INPUT
        )

        RoutineTextField(
            state = TextFieldState.INACTIVE,
            type = TextFieldType.TEXTAREA
        )

        RoutineTextField(
            state = TextFieldState.ACTIVE,
            type = TextFieldType.TEXTAREA,
            text = "바로 출입 가능합니다."
        )

        RoutineTextField(
            state = TextFieldState.DISABLED,
            type = TextFieldType.TEXTAREA
        )
    }
}