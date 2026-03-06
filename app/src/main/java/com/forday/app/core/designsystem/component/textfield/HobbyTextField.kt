package com.forday.app.core.designsystem.component.textfield

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.forday.app.core.designsystem.theme.ForDayTheme

@Composable
fun SingleLineTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "Label",
    placeholder: String = "Label",
    maxLength: Int = 10,
    enabled: Boolean = true,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Done,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    trailingIcon: @Composable (() -> Unit)? = null
) {
    var isFocused by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }

    val labelColor by animateColorAsState(
        targetValue = when {
            isFocused -> ForDayTheme.color.PrimaryBlue
            value.isNotEmpty() -> ForDayTheme.color.Gray700
            else -> Color.Transparent
        },
        label = "labelColor"
    )

    val underlineColor by animateColorAsState(
        targetValue = when {
            isFocused -> ForDayTheme.color.FocusBlue
            value.isNotEmpty() -> ForDayTheme.color.Gray700
            else -> ForDayTheme.color.StrongDivider
        },
        label = "underlineColor"
    )

    val textColor = when {
        isFocused -> ForDayTheme.color.Black
        value.isNotEmpty() -> ForDayTheme.color.Neutral900
        else -> ForDayTheme.color.Neutral600
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(16.dp)
        ) {
            if (isFocused || value.isNotEmpty()) {
                Text(
                    text = label,
                    style = ForDayTheme.typography.label12,
                    color = labelColor,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(20.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                BasicTextField(
                    value = value,
                    onValueChange = { newValue ->
                        if (newValue.length <= maxLength) {
                            onValueChange(newValue)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester)
                        .onFocusChanged { focusState ->
                            isFocused = focusState.isFocused
                        },
                    enabled = enabled,
                    textStyle = ForDayTheme.typography.label14.copy(color = textColor),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = keyboardType,
                        imeAction = imeAction
                    ),
                    keyboardActions = keyboardActions,
                    singleLine = true,
                    cursorBrush = SolidColor(ForDayTheme.color.Black),
                    decorationBox = { innerTextField ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Box(
                                modifier = Modifier.weight(1f, fill = false)
                            ) {
                                if (value.isEmpty()) {
                                    Text(
                                        text = placeholder,
                                        style = ForDayTheme.typography.label14,
                                        color = ForDayTheme.color.Neutral600,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                                innerTextField()
                            }
                        }
                    }
                )
            }

            if (trailingIcon != null) {
                Box(
                    modifier = Modifier.size(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    trailingIcon()
                }
            } else {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .background(ForDayTheme.color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(18.dp)
                            .background(
                                color = Color(0xFFF5F8FF),
                                shape = RoundedCornerShape(4.dp)
                            )
                            .border(
                                width = 1.dp,
                                color = ForDayTheme.color.Gray500,
                                shape = RoundedCornerShape(4.dp)
                            )
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(2.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(if (isFocused) 2.dp else 1.dp)
                    .background(underlineColor)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Text(
                text = "${value.length}/$maxLength",
                style = ForDayTheme.typography.label10,
                color = ForDayTheme.color.Neutral600
            )
        }
    }
}

@Preview
@Composable
fun SingleLineTextFieldPreview() {
    var text1 by remember { mutableStateOf("") }
    var text2 by remember { mutableStateOf("Label") }
    var text3 by remember { mutableStateOf("") }

    ForDayTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFEEEEEE))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            Text("Unfocused/Empty State:", style = TextStyle(fontSize = 12.sp))
            SingleLineTextField(
                value = text1,
                onValueChange = { text1 = it },
                label = "Label",
                placeholder = "Label"
            )

            Text("Filled State:", style = TextStyle(fontSize = 12.sp))
            SingleLineTextField(
                value = text2,
                onValueChange = { text2 = it },
                label = "Label",
                placeholder = "Label"
            )

            Text("Focused State (tap to focus):", style = TextStyle(fontSize = 12.sp))
            SingleLineTextField(
                value = text3,
                onValueChange = { text3 = it },
                label = "Label",
                placeholder = "Label"
            )
        }
    }

}