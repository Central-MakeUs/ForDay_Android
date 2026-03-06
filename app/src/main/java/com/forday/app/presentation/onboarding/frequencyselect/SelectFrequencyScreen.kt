package com.forday.app.presentation.onboarding.frequencyselect

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import com.forday.app.core.designsystem.component.clickable.rememberThrottledClick
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dayn.forday.R
import com.forday.app.core.designsystem.component.button.BottomNextButton
import com.forday.app.core.designsystem.component.layout.OnboardingLayout
import com.forday.app.core.designsystem.theme.ForDayTheme
import com.forday.app.presentation.modifyhobby.screen.HobbyModifyParams
import com.forday.app.core.logger.analytics.AnalyticsEvents
import com.forday.app.presentation.onboarding.OnboardingViewModel
import com.forday.app.presentation.onboarding.timeselect.ScreenMode
import timber.log.Timber

// hobbyInfoId에 따라 아이콘 리소스를 반환하는 함수
fun getHobbyIconResource(hobbyInfoId: Int?): Int {
    return when (hobbyInfoId) {
        1 -> R.drawable.ic_draw
        2 -> R.drawable.ic_health
        3 -> R.drawable.ic_book
        4 -> R.drawable.ic_music
        5 -> R.drawable.ic_running
        6 -> R.drawable.ic_cook
        7 -> R.drawable.ic_cafe
        8 -> R.drawable.ic_movie
        9 -> R.drawable.ic_camera2
        10 -> R.drawable.ic_write
        else -> R.drawable.ic_etc_hobby // 기본값
    }
}

@Composable
fun SelectFrequencyScreenRoot(
    params: HobbyModifyParams?,
    mode: ScreenMode,
    onNext: () -> Unit,
    onBack: () -> Unit,
    viewModel: OnboardingViewModel,
) {

    viewModel.logEvent(AnalyticsEvents.HOBBY_FREQUENCY_ENTRY)
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    SelectFrequencyScreen(
        hobbyName = if (mode == ScreenMode.DEFAULT) params?.hobbyName else state.selectedHobbyName,
        hobbyInfoId = if (mode == ScreenMode.DEFAULT) params?.hobbyInfoId else state.selectedHobbyId?.toInt(),
        selectedTime = if (mode == ScreenMode.DEFAULT && params != null) {
            val minutes = params.hobbyTimeMinutes
            if (minutes == 60 || minutes == 120) "${minutes / 60}시간" else "${minutes}분"
        } else if (state.selectedMinutes == 60 || state.selectedMinutes == 120) {
            "${state.selectedMinutes!! / 60}시간"
        } else {
            "${state.selectedMinutes}분"
        },
        selectedFrequency = state.selectedFrequency,
        journeyDays = if (mode == ScreenMode.DEFAULT) params?.goalDays else null,
        onBack = {
            viewModel.logEvent(AnalyticsEvents.HOBBY_FREQUENCY_BACK)
            onBack()
        },
        onNext = { executionCount ->
            if (mode == ScreenMode.DEFAULT) {
                viewModel.modifyHobbyExecutionCount(params!!.hobbyId.toLong(), executionCount)
            }
            onNext()
        },
        onFrequencySelect = { frequency ->
            viewModel.logEvent(AnalyticsEvents.hobbyWeeklyCount(frequency))
            if (mode == ScreenMode.ONBOARDING) {
                viewModel.saveFrequency(frequency)
            }
        },
        params = params,
        mode = mode,
    )
}
@Composable
fun SelectFrequencyScreen(
    hobbyName: String? = "독서",
    hobbyInfoId: Int? = null,
    selectedTime: String = "30분",
    selectedFrequency: Int? = null,
    journeyDays: Int? = null,
    params: HobbyModifyParams?,
    mode: ScreenMode,
    onBack: () -> Unit = {},
    onNext: (Int) -> Unit = {},
    onFrequencySelect: (Int) -> Unit = {},
) {
    // DEFAULT 모드일 때는 로컬 상태로 관리, ONBOARDING일 때는 ViewModel 상태 사용
    val localSelectedFrequency = remember(params?.executionCount, selectedFrequency) {
        mutableStateOf(
            if (mode == ScreenMode.DEFAULT) {
                params?.executionCount
            } else {
                selectedFrequency
            }
        )
    }

    // 실제 사용할 횟수 값
    val currentFrequency = if (mode == ScreenMode.DEFAULT) {
        localSelectedFrequency.value
    } else {
        selectedFrequency
    }

    OnboardingLayout(
        title = "실행 횟수",
        currentStep = 4,
        totalSteps = 5,
        mode = mode,
        onBack = { onBack() }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(ForDayTheme.color.Neutral50)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {

                Spacer(modifier = Modifier.height(8.dp))

                // Content
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                ) {
                    // Title and Description
                    FrequencyTitleSection(hobbyName = hobbyName)

                    Spacer(modifier = Modifier.height(24.dp))

                    // Hobby Summary Card - hobbyInfoId 전달
                    HobbySummaryCard(
                        hobbyName = hobbyName,
                        hobbyInfoId = if (mode == ScreenMode.DEFAULT) params?.hobbyInfoId else hobbyInfoId,
                        selectedTime = selectedTime,
                        selectedFrequency = currentFrequency,
                        journeyDays = journeyDays,
                    )

                    Spacer(modifier = Modifier.height(40.dp))

                    // Frequency Selector
                    FrequencySelector(
                        selectedFrequency = currentFrequency,
                        onFrequencySelected = { frequency ->
                            if (mode == ScreenMode.DEFAULT) {
                                // DEFAULT 모드: 로컬 상태만 업데이트
                                localSelectedFrequency.value = frequency
                            } else {
                                // ONBOARDING 모드: ViewModel에 즉시 저장
                                onFrequencySelect(frequency)
                            }
                        }
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                BottomNextButton(
                    text = if (mode == ScreenMode.DEFAULT) "변경하기" else "다음",
                    enabled = currentFrequency != null && currentFrequency > 0,
                    onNext = {
                        currentFrequency?.let { onNext(it) }
                    },
                    backgroundColor = ForDayTheme.color.Neutral50
                )
            }
        }
    }
}

@Composable
fun FrequencyTitleSection(hobbyName: String?) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = "주 몇 회 하실 거에요?",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = ForDayTheme.color.Black
        )

        Text(
            text = buildAnnotatedString {
                withStyle(SpanStyle(color = ForDayTheme.color.Secondary003)) {
                    append(hobbyName)
                }
                withStyle(SpanStyle(color = ForDayTheme.color.Gray800)) {
                    append("를 일주일에 몇 번 할까요?\n")
                    append("(처음에는 작게 시작해도 좋아요! 언제든지 변경 가능해요.)")
                }
            },
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal,
            lineHeight = 19.6.sp
        )
    }
}

@Composable
fun HobbySummaryCard(
    hobbyName: String?,
    hobbyInfoId: Int? = null,
    selectedTime: String,
    selectedFrequency: Int?,
    journeyDays: Int? = null,
) {
    val isSelected = selectedFrequency != null

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(8.dp),
                spotColor = Color.Black.copy(alpha = 0.06f),
                ambientColor = Color.Black.copy(alpha = 0.06f)
            ),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = ForDayTheme.color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Book icon - hobbyInfoId에 따라 동적으로 아이콘 표시
            Box(
                modifier = Modifier.size(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = getHobbyIconResource(hobbyInfoId)),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = Color.Unspecified
                )
            }

            // Text content
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                // Time and Frequency info
                Row(
                    horizontalArrangement = Arrangement.spacedBy(3.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = selectedTime,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Normal,
                        color = ForDayTheme.color.Neutral600,
                        lineHeight = 14.sp
                    )

                    if (selectedFrequency != null) {
                        Box(
                            modifier = Modifier
                                .size(2.dp)
                                .background(ForDayTheme.color.Neutral600, CircleShape)
                        )

                        Text(
                            text = "주 ${selectedFrequency}회",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Normal,
                            color = ForDayTheme.color.StrongDivider,
                            lineHeight = 14.sp
                        )
                    }

                    if (journeyDays != null) {
                        Box(
                            modifier = Modifier
                                .size(2.dp)
                                .background(ForDayTheme.color.Neutral600, CircleShape)
                        )

                        Text(
                            text = if (journeyDays == 0) "기간 미지정" else "${journeyDays}일",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Normal,
                            color = ForDayTheme.color.Neutral600,
                            lineHeight = 14.sp
                        )
                    }
                }

                hobbyName?.let {
                    Text(
                        text = it,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = ForDayTheme.color.Gray800
                    )
                }
            }

            // Check icon
            Box(
                modifier = Modifier.size(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.onoff),
                    contentDescription = if (isSelected) "선택됨" else "선택 안됨",
                    modifier = Modifier.size(24.dp),
                    tint = if (isSelected) ForDayTheme.color.Orange03 else ForDayTheme.color.Gray03
                )
            }
        }
    }
}

@Composable
fun FrequencySelector(
    selectedFrequency: Int?,
    onFrequencySelected: (Int) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // Recommended label - positioned above number 2
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                // Spacer for button 1
                Spacer(modifier = Modifier.weight(1f))

                // "추천" text aligned to button 2
                Text(
                    text = "추천",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Normal,
                    color = ForDayTheme.color.Primary001,
                    textAlign = TextAlign.Center,
                    lineHeight = 14.sp,
                    modifier = Modifier.weight(1f)
                )

                // Spacer for remaining buttons (3-7)
                Spacer(modifier = Modifier.weight(5f))
            }
        }

        // Number selector row (1-7)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            (1..7).forEach { number ->
                FrequencyButton(
                    number = number,
                    isSelected = selectedFrequency == number,
                    isRecommended = number == 2,
                    isNextSelected = selectedFrequency == number + 1,
                    onClick = { onFrequencySelected(number) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun FrequencyButton(
    number: Int,
    isSelected: Boolean,
    isRecommended: Boolean,
    isNextSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = when {
        isSelected -> ForDayTheme.color.Primary03
        else -> ForDayTheme.color.White
    }

    val borderColor = when {
        isSelected -> ForDayTheme.color.Primary001
        else -> ForDayTheme.color.Gray03
    }

    val rightBorderColor = when {
        isNextSelected -> ForDayTheme.color.Primary001
        isSelected -> ForDayTheme.color.Primary001
        else -> ForDayTheme.color.Gray03
    }

    val shape = when (number) {
        1 -> RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp)
        7 -> RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp)
        else -> RoundedCornerShape(0.dp)
    }

    Box(
        modifier = modifier
            .height(40.dp)
            .background(backgroundColor, shape)
            .drawBehind {
                val strokeWidth = 1.dp.toPx()
                val halfStroke = strokeWidth / 2
                val radius = 8.dp.toPx()

                when (number) {
                    1 -> {
                        // 버튼 1: 왼쪽 둥근 모서리 + top + bottom (borderColor)
                        val path = Path().apply {
                            // top-left 둥근 모서리에서 시작
                            moveTo(halfStroke + radius, halfStroke)
                            // top 직선
                            lineTo(size.width, halfStroke)
                        }
                        drawPath(path, color = borderColor, style = Stroke(width = strokeWidth))

                        val bottomPath = Path().apply {
                            moveTo(size.width, size.height - halfStroke)
                            // bottom 직선
                            lineTo(halfStroke + radius, size.height - halfStroke)
                            // bottom-left 둥근 모서리
                            arcTo(
                                rect = androidx.compose.ui.geometry.Rect(
                                    halfStroke, size.height - halfStroke - radius * 2,
                                    halfStroke + radius * 2, size.height - halfStroke
                                ),
                                startAngleDegrees = 90f,
                                sweepAngleDegrees = 90f,
                                forceMoveTo = false
                            )
                            // left 직선
                            lineTo(halfStroke, halfStroke + radius)
                            // top-left 둥근 모서리
                            arcTo(
                                rect = androidx.compose.ui.geometry.Rect(
                                    halfStroke, halfStroke,
                                    halfStroke + radius * 2, halfStroke + radius * 2
                                ),
                                startAngleDegrees = 180f,
                                sweepAngleDegrees = 90f,
                                forceMoveTo = false
                            )
                        }
                        drawPath(bottomPath, color = borderColor, style = Stroke(width = strokeWidth))

                        // 오른쪽 border (rightBorderColor 별도 적용)
                        drawLine(
                            color = rightBorderColor,
                            start = Offset(size.width - halfStroke, 0f),
                            end = Offset(size.width - halfStroke, size.height),
                            strokeWidth = strokeWidth
                        )
                    }
                    7 -> {
                        // 버튼 7: 왼쪽 직각 + 오른쪽 둥근 모서리
                        val path = Path().apply {
                            moveTo(0f, halfStroke)
                            // top 직선
                            lineTo(size.width - halfStroke - radius, halfStroke)
                            // top-right 둥근 모서리
                            arcTo(
                                rect = androidx.compose.ui.geometry.Rect(
                                    size.width - halfStroke - radius * 2, halfStroke,
                                    size.width - halfStroke, halfStroke + radius * 2
                                ),
                                startAngleDegrees = 270f,
                                sweepAngleDegrees = 90f,
                                forceMoveTo = false
                            )
                            // right 직선
                            lineTo(size.width - halfStroke, size.height - halfStroke - radius)
                            // bottom-right 둥근 모서리
                            arcTo(
                                rect = androidx.compose.ui.geometry.Rect(
                                    size.width - halfStroke - radius * 2, size.height - halfStroke - radius * 2,
                                    size.width - halfStroke, size.height - halfStroke
                                ),
                                startAngleDegrees = 0f,
                                sweepAngleDegrees = 90f,
                                forceMoveTo = false
                            )
                            // bottom 직선
                            lineTo(0f, size.height - halfStroke)
                        }
                        drawPath(path, color = borderColor, style = Stroke(width = strokeWidth))
                    }
                    else -> {
                        // 버튼 2~6: 직각 border (기존 방식)
                        // 위쪽 border
                        drawLine(
                            color = borderColor,
                            start = Offset(0f, halfStroke),
                            end = Offset(size.width, halfStroke),
                            strokeWidth = strokeWidth
                        )
                        // 아래쪽 border
                        drawLine(
                            color = borderColor,
                            start = Offset(0f, size.height - halfStroke),
                            end = Offset(size.width, size.height - halfStroke),
                            strokeWidth = strokeWidth
                        )
                        // 오른쪽 border
                        drawLine(
                            color = rightBorderColor,
                            start = Offset(size.width - halfStroke, 0f),
                            end = Offset(size.width - halfStroke, size.height),
                            strokeWidth = strokeWidth
                        )
                    }
                }
            }
            .clip(shape)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick = rememberThrottledClick { onClick() }
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = number.toString(),
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal,
            color = ForDayTheme.color.Gray800,
            textAlign = TextAlign.Center,
            lineHeight = 19.6.sp
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SelectFrequencyScreenPreview() {
    ForDayTheme {
        SelectFrequencyScreen(
            hobbyName = "독서",
            hobbyInfoId = 3, // 예시: 독서는 id 3
            selectedTime = "30분",
            selectedFrequency = 2,
            params = null,
            mode = ScreenMode.ONBOARDING,
            onBack = {},
            onNext = {},
            onFrequencySelect = {},
        )
    }
}