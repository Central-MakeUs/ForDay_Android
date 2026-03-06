package com.forday.app.core.designsystem.component.textfield

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.forday.app.core.designsystem.component.clickable.NoRippleInteractionSource
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dayn.forday.R
import com.forday.app.core.designsystem.component.clickable.rememberThrottledClick
import com.forday.app.core.designsystem.theme.ForDayTheme


/**
 * ForDay 입력 필드
 *
 * @param value 입력 값
 * @param onValueChange 값 변경 이벤트
 * @param label 라벨 텍스트
 * @param placeholder 플레이스홀더 텍스트
 * @param modifier Modifier
 * @param state 필드 상태
 * @param errorMessage 에러 메시지 (null이면 표시 안 함)
 * @param successMessage 성공 메시지 (null이면 표시 안 함)
 * @param isPassword 비밀번호 필드 여부
 * @param trailingButton 우측 버튼 텍스트 (null이면 표시 안 함)
 * @param onTrailingButtonClick 우측 버튼 클릭 이벤트
 * @param keyboardOptions 키보드 옵션
 * @param keyboardActions 키보드 액션
 */
@Composable
fun NicknameTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String = label,
    modifier: Modifier = Modifier,
    state: TextFieldState = TextFieldState.INACTIVE,
    errorMessage: String? = null,
    successMessage: String? = null,
    isPassword: Boolean = false,
    trailingButton: String? = null,
    onTrailingButtonClick: () -> Unit = {},
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default
) {
    var passwordVisible by remember { mutableStateOf(false) }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .background(
                    color = when (state) {
                        TextFieldState.INACTIVE, TextFieldState.ACTIVE -> Color(0xFFF2F2F2)
                        TextFieldState.DISABLED -> Color(0xFFD1D1D1)
                    },
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = if (state == TextFieldState.ACTIVE) {
                    Arrangement.spacedBy(2.dp)
                } else {
                    Arrangement.spacedBy(4.dp)
                }
            ) {
                Text(
                    text = label,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color(0xFF9E9E9E),
                    lineHeight = 16.8.sp
                )

                if (state == TextFieldState.ACTIVE && value.isNotEmpty()) {
                    BasicTextField(
                        value = value,
                        onValueChange = onValueChange,
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF1E1E1E),
                            lineHeight = 26.sp,
                            letterSpacing = (-0.48).sp
                        ),
                        enabled = state != TextFieldState.DISABLED,
                        singleLine = true,
                        visualTransformation = if (isPassword && !passwordVisible) {
                            PasswordVisualTransformation()
                        } else {
                            VisualTransformation.None
                        },
                        keyboardOptions = keyboardOptions,
                        keyboardActions = keyboardActions,
                        cursorBrush = SolidColor(Color(0xFF1E1E1E))
                    )
                } else {
                    Text(
                        text = placeholder,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = when (state) {
                            TextFieldState.INACTIVE -> Color(0xFFB5B5B5)
                            TextFieldState.ACTIVE -> Color(0xFFB5B5B5)
                            TextFieldState.DISABLED -> Color(0xFF9E9E9E)
                        },
                        lineHeight = 16.sp
                    )
                }
            }

            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(top = 4.dp)
            ) {
                when {
                    // 우측 버튼
                    trailingButton != null -> {
                        Button(
                            onClick = rememberThrottledClick(onClick = onTrailingButtonClick),
                            modifier = Modifier.height(28.dp),
                            interactionSource = remember { NoRippleInteractionSource() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF1E1E1E)
                            ),
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = trailingButton,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Normal,
                                color = Color.White,
                                lineHeight = 16.8.sp
                            )
                        }
                    }
                    isPassword && state != TextFieldState.INACTIVE && passwordVisible -> {
                        IconButton(
                            onClick = rememberThrottledClick {
                                passwordVisible = !passwordVisible
                            },
                            modifier = Modifier.size(24.dp),
                            interactionSource = remember { NoRippleInteractionSource() }
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_visibility_on),
                                contentDescription = if (passwordVisible) "비밀번호 숨기기" else "비밀번호 보기",
                                tint = Color.Unspecified
                            )
                        }
                    }
                }
            }
        }

        // 에러 메시지
        if (errorMessage != null) {
            Text(
                text = errorMessage,
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal,
                color = Color(0xFFE85A4F),
                lineHeight = 16.8.sp,
                modifier = Modifier.padding(start = 16.dp)
            )
        }

        // 성공 메시지
        if (successMessage != null) {
            Text(
                text = successMessage,
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal,
                color = Color(0xFF4683FF),
                lineHeight = 16.8.sp,
                modifier = Modifier.padding(start = 16.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ForDayTextFieldInactivePreview() {
    ForDayTheme {
        Box(modifier = Modifier.padding(20.dp)) {
            NicknameTextField(
                value = "",
                onValueChange = {},
                label = "이메일 주소",
                state = TextFieldState.INACTIVE,
                isPassword = true,
                errorMessage = "이미 사용중인 이메일 입니다."
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ForDayTextFieldActivePreview() {
    ForDayTheme {
        Box(modifier = Modifier.padding(20.dp)) {
            NicknameTextField(
                value = "example@email.com",
                onValueChange = {},
                label = "이메일 주소",
                state = TextFieldState.ACTIVE,
                isPassword = true,
                errorMessage = "이미 사용중인 이메일 입니다."
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ForDayTextFieldDisabledPreview() {
    ForDayTheme {
        Box(modifier = Modifier.padding(20.dp)) {
            NicknameTextField(
                value = "",
                onValueChange = {},
                label = "이메일 주소",
                state = TextFieldState.DISABLED,
                errorMessage = "이미 사용중인 이메일 입니다."
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ForDayTextFieldWithButtonPreview() {
    ForDayTheme {
        Box(modifier = Modifier.padding(20.dp)) {
            NicknameTextField(
                value = "example@email.com",
                onValueChange = {},
                label = "이메일 주소",
                state = TextFieldState.ACTIVE,
                trailingButton = "중복확인",
                onTrailingButtonClick = {},
                successMessage = "사용 가능합니다."
            )
        }
    }
}