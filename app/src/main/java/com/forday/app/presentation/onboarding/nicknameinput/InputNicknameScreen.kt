package com.forday.app.presentation.onboarding.nicknameinput

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.forday.app.core.designsystem.component.clickable.rememberThrottledClick
import com.forday.app.core.designsystem.component.clickable.NoRippleInteractionSource
import com.forday.app.core.designsystem.theme.ForDayTheme
import com.forday.app.core.logger.analytics.AnalyticsEvents
import com.forday.app.presentation.onboarding.OnboardingViewModel
import com.forday.app.presentation.onboarding.OnboardingUiState
import timber.log.Timber

// 반응형 Dimensions
@Composable
fun rememberNicknameScreenDimensions(): NicknameScreenDimensions {
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current

    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp

    return remember(screenWidth, screenHeight) {
        NicknameScreenDimensions(
            screenWidth = screenWidth,
            screenHeight = screenHeight,
            horizontalPadding = when {
                screenWidth < 360.dp -> 16.dp
                screenWidth > 600.dp -> 32.dp
                else -> 20.dp
            },
            topSpacing = when {
                screenHeight < 640.dp -> 40.dp
                screenHeight > 800.dp -> 80.dp
                else -> 60.dp
            },
            titleToInputSpacing = when {
                screenHeight < 640.dp -> 12.dp
                else -> 16.dp
            },
            buttonHeight = when {
                screenHeight < 640.dp -> 52.dp
                else -> 56.dp
            },
            bottomContainerHeight = when {
                screenHeight < 640.dp -> 80.dp
                else -> 88.dp
            }
        )
    }
}

data class NicknameScreenDimensions(
    val screenWidth: Dp,
    val screenHeight: Dp,
    val horizontalPadding: Dp,
    val topSpacing: Dp,
    val titleToInputSpacing: Dp,
    val buttonHeight: Dp,
    val bottomContainerHeight: Dp
)

@Composable
fun InputNicknameScreenRoot(
    onNext: () -> Unit,
    viewModel: OnboardingViewModel
) {
    viewModel.logEvent(AnalyticsEvents.NICKNAME_INPUT_SCREEN)
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // ✅ 닉네임 등록 성공 시 자동으로 다음 화면으로
    LaunchedEffect(uiState.nicknameRegisterSuccess) {
        if (uiState.nicknameRegisterSuccess) {
            viewModel.saveIsNicknameSet(true)
            onNext()
        }
    }

    InputNicknameScreen(
        uiState = uiState,
        onNext = {
            viewModel.logEvent(AnalyticsEvents.NICKNAME_REGISTER_CLICK)
            viewModel.registerNickname(uiState.selectedHobbyName)
            // ✅ 여기서는 API 호출만! navigation은 LaunchedEffect에서 처리
        },
        onCheckDuplicate = { nickname ->
            viewModel.logEvent(AnalyticsEvents.currentInputNickname(nickname))
            viewModel.getIsNicknameDuplicate(nickname)
        },
        onNicknameChange = {
            viewModel.resetNicknameCheck()
        }
    )
}

@Composable
fun InputNicknameScreen(
    uiState: OnboardingUiState = OnboardingUiState(),
    onNext: () -> Unit = {},
    onCheckDuplicate: (String) -> Unit = {},
    onNicknameChange: () -> Unit = {}
) {
    val dimensions = rememberNicknameScreenDimensions()
    var nickname by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var successMessage by remember { mutableStateOf("") }
    var hasFocused by remember { mutableStateOf(false) }
    var isDuplicateChecked by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    // ✅ UiState 변경 시마다 메시지 업데이트
    LaunchedEffect(uiState.nicknameCheckMessage, uiState.isNicknameChecked) {
        if (uiState.nicknameCheckMessage.isNotEmpty()) {
            when (uiState.isNicknameChecked) {
                true -> {
                    successMessage = uiState.nicknameCheckMessage
                    errorMessage = ""
                    isDuplicateChecked = true
                    focusManager.clearFocus()
                }
                false -> {
                    errorMessage = uiState.nicknameCheckMessage
                    successMessage = ""
                    isDuplicateChecked = true
                }
            }
        }
    }

    // 유효성 검사 함수
    fun validateNickname(): Boolean {
        if (hasFocused) {
            errorMessage = when {
                nickname.isBlank() -> "필수 입력 항목입니다."
                !nickname.matches(Regex("^[가-힣a-zA-Z0-9]+\$")) -> "한글, 영어, 숫자만 사용할 수 있습니다."
                else -> ""
            }
            return errorMessage.isEmpty()
        }
        return false
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ForDayTheme.color.White)
            .clickable(
                onClick = rememberThrottledClick {
                    val isValid = validateNickname()
                    if (isValid) {
                        focusManager.clearFocus()
                    }
                },
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            )
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Spacer(modifier = Modifier.height(dimensions.topSpacing))

            // Title and Description
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = dimensions.horizontalPadding),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Column {
                    Text(
                        text = "뉴 포비님,",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = ForDayTheme.color.Neutral900,
                        lineHeight = 24.sp
                    )
                    Text(
                        text = "어떻게 불리면 좋을까요?",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = ForDayTheme.color.Neutral900,
                        lineHeight = 24.sp
                    )
                }

                Text(
                    text = "포데이에서는 사용자를 '포비'라고 불러요.\n포비님의 이름을 알려주세요.",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    color = ForDayTheme.color.Gray800,
                    lineHeight = 19.6.sp
                )
            }

            Spacer(modifier = Modifier.height(dimensions.titleToInputSpacing))

            // Input Field with Duplicate Check Button
            NicknameInputField(
                nickname = nickname,
                errorMessage = errorMessage,
                successMessage = successMessage,
                isLoading = uiState.isLoading,
                dimensions = dimensions,
                onNicknameChange = { newValue ->
                    if (newValue.length <= 10) {
                        nickname = newValue
                        errorMessage = ""
                        successMessage = ""
                        isDuplicateChecked = false
                        onNicknameChange()
                    }
                },
                onCheckDuplicate = {
                    onCheckDuplicate(nickname)
                },
                onFocusChanged = { isFocused ->
                    if (isFocused) {
                        hasFocused = true
                        if (!isDuplicateChecked) {
                            errorMessage = ""
                            successMessage = ""
                        }
                    }
                },
                onEnterPressed = {
                    val isValid = validateNickname()
                    if (isValid) {
                        focusManager.clearFocus()
                    }
                }
            )
        }

        Timber.e("@#####@@@!!@! $successMessage, ${uiState.isNicknameChecked}")

        // Bottom Button
        val isNicknameValid = successMessage.isNotEmpty() &&
                uiState.isNicknameChecked == true

        BottomButton(
            enabled = isNicknameValid,
            onClick = onNext,
            dimensions = dimensions
        )
    }
}

@Composable
fun NicknameInputField(
    nickname: String,
    errorMessage: String,
    successMessage: String = "",
    isLoading: Boolean = false,
    dimensions: NicknameScreenDimensions,
    onNicknameChange: (String) -> Unit,
    onCheckDuplicate: () -> Unit,
    onFocusChanged: (Boolean) -> Unit,
    onEnterPressed: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = dimensions.horizontalPadding),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Input field container
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 60.dp)
                .background(
                    color = Color(0xFFF9F9F9),
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(
                    start = 16.dp,
                    end = when {
                        dimensions.screenWidth < 360.dp -> 8.dp
                        else -> 16.dp
                    },
                    top = 8.dp,
                    bottom = 8.dp
                )
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Text Input Area
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Label
                    Text(
                        text = "닉네임",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal,
                        color = ForDayTheme.color.Gray500,
                        lineHeight = 16.8.sp
                    )

                    // Text Input
                    BasicTextField(
                        value = nickname,
                        onValueChange = onNicknameChange,
                        modifier = Modifier
                            .fillMaxWidth()
                            .onFocusChanged { focusState ->
                                onFocusChanged(focusState.isFocused)
                            },
                        textStyle = TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (nickname.isEmpty())
                                ForDayTheme.color.StrongDivider
                            else
                                ForDayTheme.color.Neutral900,
                            lineHeight = 16.sp
                        ),
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                onEnterPressed()
                            }
                        ),
                        singleLine = true,
                        decorationBox = { innerTextField ->
                            Box {
                                if (nickname.isEmpty()) {
                                    Text(
                                        text = "포비님의 닉네임을 입력해 주세요.",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = ForDayTheme.color.StrongDivider,
                                        lineHeight = 16.sp
                                    )
                                }
                                innerTextField()
                            }
                        }
                    )
                }

                // Duplicate Check Button
                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = rememberThrottledClick { onCheckDuplicate() },
                    modifier = Modifier
                        .heightIn(min = 28.dp)
                        .padding(0.dp),
                    shape = RoundedCornerShape(6.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ForDayTheme.color.Neutral900,
                        contentColor = ForDayTheme.color.White
                    ),
                    contentPadding = PaddingValues(
                        horizontal = when {
                            dimensions.screenWidth < 360.dp -> 8.dp
                            else -> 10.dp
                        },
                        vertical = 5.dp
                    ),
                    enabled = nickname.isNotEmpty() && !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = ForDayTheme.color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = "중복확인",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.W400,
                            color = ForDayTheme.color.White,
                            lineHeight = 16.8.sp
                        )
                    }
                }
            }
        }

        // Error Message
        if (errorMessage.isNotEmpty()) {
            Text(
                text = errorMessage,
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal,
                color = Color(0xFFFF0000),
                lineHeight = 16.8.sp,
                modifier = Modifier.padding(horizontal = 4.dp)
            )
        }

        // Success Message
        if (successMessage.isNotEmpty()) {
            Text(
                text = successMessage,
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal,
                color = Color(0xFF00AA00),
                lineHeight = 16.8.sp,
                modifier = Modifier.padding(horizontal = 4.dp)
            )
        }
    }
}

@Composable
fun BoxScope.BottomButton(
    enabled: Boolean,
    onClick: () -> Unit,
    dimensions: NicknameScreenDimensions
) {
    Box(
        modifier = Modifier
            .align(Alignment.BottomCenter)
            .fillMaxWidth()
            .height(dimensions.bottomContainerHeight)
    ) {
        // Gradient background
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0f),
                            Color.White
                        ),
                        startY = 0f,
                        endY = with(LocalDensity.current) {
                            dimensions.bottomContainerHeight.toPx()
                        }
                    )
                )
        )

        // Button
        Button(
            onClick = rememberThrottledClick { onClick() },
            enabled = enabled,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(
                    horizontal = dimensions.horizontalPadding,
                    vertical = when {
                        dimensions.screenHeight < 640.dp -> 12.dp
                        else -> 16.dp
                    }
                )
                .fillMaxWidth()
                .height(dimensions.buttonHeight),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (enabled)
                    ForDayTheme.color.Orange01
                else
                    ForDayTheme.color.Gray03,
                contentColor = ForDayTheme.color.White,
                disabledContainerColor = ForDayTheme.color.Gray03,
                disabledContentColor = ForDayTheme.color.White
            )
        ) {
            Text(
                text = "다음",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 760)
@Composable
private fun Preview_InputNicknameScreen() {
    ForDayTheme {
        InputNicknameScreen()
    }
}

@Preview(showBackground = true, widthDp = 320, heightDp = 568, name = "Small Screen")
@Composable
private fun Preview_InputNicknameScreen_Small() {
    ForDayTheme {
        InputNicknameScreen()
    }
}

@Preview(showBackground = true, widthDp = 428, heightDp = 926, name = "Large Screen")
@Composable
private fun Preview_InputNicknameScreen_Large() {
    ForDayTheme {
        InputNicknameScreen()
    }
}

@Preview(showBackground = true, widthDp = 600, heightDp = 960, name = "Tablet")
@Composable
private fun Preview_InputNicknameScreen_Tablet() {
    ForDayTheme {
        InputNicknameScreen()
    }
}