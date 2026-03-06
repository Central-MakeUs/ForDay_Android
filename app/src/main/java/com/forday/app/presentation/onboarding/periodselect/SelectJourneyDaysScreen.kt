package com.forday.app.presentation.onboarding.periodselect

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber


enum class JourneyMode {
    FREE,      // 기간 미지정 (자율 모드)
    FORDAY_66  // 66일 (포데이 모드)
}

data class JourneyOption(
    val mode: JourneyMode,
    val title: String,
    val description: String,
    val characterIcon: Int
)

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
fun SelectJourneyDaysScreenRoot(
    params: HobbyModifyParams?,
    mode: ScreenMode,
    onNext: () -> Unit,
    onBack: () -> Unit,
    goHome: () -> Unit,
    viewModel: OnboardingViewModel,
) {
    viewModel.logEvent(AnalyticsEvents.HOBBY_JOURNEY_DATE_SCREEN)
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    Timber.e("@@@@@@@@@@@111"+state.isNicknameSet+", "+mode)

    val scope = rememberCoroutineScope()
    SelectJourneyDaysScreen(
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
        selectedFrequency = if (mode == ScreenMode.DEFAULT) params?.executionCount else state.selectedFrequency,
        selectedJourneyMode = state.selectedJourneyMode,
        onBack = {
            viewModel.logEvent(AnalyticsEvents.HOBBY_JOURNEY_DATE_BACK)
            scope.launch {
                delay(400L)
                onBack()
            }
        },
        onNext = { journeyMode ->
            scope.launch {
                if (mode == ScreenMode.DEFAULT) {
                    val goalDays = when (journeyMode) {
                        JourneyMode.FORDAY_66 -> true
                        JourneyMode.FREE -> false
                    }
                    viewModel.modifyHobbyGoalDays(params!!.hobbyId.toLong(), goalDays)
                } else {
                    viewModel.logEvent(AnalyticsEvents.ONBOARDING_SUCCESS)
                    viewModel.saveIsOnboardingCompleted(true)
                    viewModel.createHobby(
                        state.selectedHobbyId,
                        state.selectedHobbyName,
                        state.selectedMinutes,
                        state.selectedPurpose,
                        state.selectedFrequency,
                        state.selectedJourneyMode
                    )
                }
                delay(400L)                                     // ✅ 공통 2초 지연
                if (mode == ScreenMode.DEFAULT) {
                    onNext()
                } else {
                    if (state.isNicknameSet == true) {
                        goHome()
                    } else {
                        onNext()
                    }
                }
            }
        },
        onJourneyModeSelect = { journeyMode ->
            viewModel.logEvent(AnalyticsEvents.selectedJourneyDate(journeyMode.toString()))
            // ONBOARDING 모드일 때만 즉시 저장
            if (mode == ScreenMode.ONBOARDING) {
                viewModel.selectJourneyMode(journeyMode)
            }
        },
        params = params,
        mode = mode
    )
}

@Composable
fun SelectJourneyDaysScreen(
    params: HobbyModifyParams?,
    mode: ScreenMode,
    hobbyName: String? = "독서",
    hobbyInfoId: Int? = null, // hobbyInfoId 파라미터 추가
    selectedTime: String = "30분",
    selectedFrequency: Int? = 2,
    selectedJourneyMode: JourneyMode? = null,
    onBack: () -> Unit = {},
    onNext: (JourneyMode) -> Unit = {},
    onJourneyModeSelect: (JourneyMode) -> Unit = {}
) {
    // DEFAULT 모드일 때는 로컬 상태로 관리, ONBOARDING일 때는 ViewModel 상태 사용
    val localSelectedJourneyMode = remember(params?.goalDays, selectedJourneyMode) {
        mutableStateOf(
            if (mode == ScreenMode.DEFAULT) {
                // DEFAULT: params.goalDays를 JourneyMode로 변환
                when (params?.goalDays) {
                    66 -> JourneyMode.FORDAY_66
                    0 -> JourneyMode.FREE
                    else -> null
                }
            } else {
                // ONBOARDING: null (아무것도 선택 안됨)
                null
            }
        )
    }

    // 실제 사용할 JourneyMode 값
    val currentJourneyMode = if (mode == ScreenMode.DEFAULT) {
        localSelectedJourneyMode.value
    } else {
        selectedJourneyMode
    }

    val journeyOptions = listOf(
        JourneyOption(
            mode = JourneyMode.FREE,
            title = "기간 미지정 (자율 모드)",
            description = "정해두지 않고, 흐름대로",
            characterIcon = R.drawable.ic_character_period_none
        ),
        JourneyOption(
            mode = JourneyMode.FORDAY_66,
            title = "66일\n(포데이 모드)",
            description = "생활에 자연스럽게 스며드는 기간",
            characterIcon = R.drawable.ic_character_sixtysix
        )
    )

    OnboardingLayout(
        title = "여정일",
        currentStep = 5,
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

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 20.dp)
                ) {
                    JourneyDaysTitleSection()

                    Spacer(modifier = Modifier.height(24.dp))

                    HobbySummaryCard(
                        hobbyName = hobbyName,
                        hobbyInfoId = if (mode == ScreenMode.DEFAULT) params?.hobbyInfoId else hobbyInfoId, // hobbyInfoId 전달
                        selectedTime = selectedTime,
                        selectedFrequency = selectedFrequency,
                        selectedJourneyMode = currentJourneyMode
                    )

                    Spacer(modifier = Modifier.height(40.dp))

                    JourneyModeSelector(
                        options = journeyOptions,
                        selectedMode = currentJourneyMode,
                        onModeSelect = { journeyMode ->
                            if (mode == ScreenMode.DEFAULT) {
                                // DEFAULT 모드: 로컬 상태만 업데이트
                                localSelectedJourneyMode.value = journeyMode
                            } else {
                                // ONBOARDING 모드: ViewModel에 즉시 저장
                                onJourneyModeSelect(journeyMode)
                            }
                        }
                    )
                }

                BottomNextButton(
                    text = if (mode == ScreenMode.DEFAULT) "변경하기" else "완료",
                    enabled = currentJourneyMode != null,
                    onNext = { currentJourneyMode?.let { onNext(it) } },
                    backgroundColor = ForDayTheme.color.Neutral50
                )
            }
        }
    }
}

@Composable
fun JourneyDaysTitleSection() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = "이 취미, 어떻게 이어가볼까요?",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = ForDayTheme.color.Neutral900
        )

        Column {
            Text(
                text = "며칠동안 해볼까요?",
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color = ForDayTheme.color.Gray800,
                lineHeight = 19.6.sp
            )
            Text(
                text = "하루에 1개씩 꾸준히 기록을 남겨봐요.",
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color = ForDayTheme.color.Gray800,
                lineHeight = 19.6.sp
            )
        }
    }
}

@Composable
fun HobbySummaryCard(
    hobbyName: String?,
    hobbyInfoId: Int? = null, // hobbyInfoId 파라미터 추가
    selectedTime: String,
    selectedFrequency: Int?,
    selectedJourneyMode: JourneyMode?
) {
    val isSelected = selectedJourneyMode != null

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
                // Time, Frequency, and Journey Days info
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
                        // Dot separator
                        Box(
                            modifier = Modifier
                                .size(2.dp)
                                .background(ForDayTheme.color.Neutral600, CircleShape)
                        )

                        Text(
                            text = "주 ${selectedFrequency}회",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Normal,
                            color = ForDayTheme.color.Neutral600,
                            lineHeight = 14.sp
                        )
                    }

                    if (selectedJourneyMode != null) {
                        // Dot separator
                        Box(
                            modifier = Modifier
                                .size(2.dp)
                                .background(ForDayTheme.color.Neutral600, CircleShape)
                        )

                        Text(
                            text = when (selectedJourneyMode) {
                                JourneyMode.FORDAY_66 -> "66일"
                                JourneyMode.FREE -> "기간 미지정"
                            },
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Normal,
                            color = ForDayTheme.color.StrongDivider,
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
fun JourneyModeSelector(
    options: List<JourneyOption>,
    selectedMode: JourneyMode?,
    onModeSelect: (JourneyMode) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        options.forEach { option ->
            JourneyModeCard(
                option = option,
                isSelected = selectedMode == option.mode,
                onClick = { onModeSelect(option.mode) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun JourneyModeCard(
    option: JourneyOption,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    onClick = onClick,
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                )
                .border(
                    width = 1.dp,
                    color = if (isSelected) ForDayTheme.color.Primary001 else ForDayTheme.color.Gray03,
                    shape = RoundedCornerShape(16.dp)
                ),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = ForDayTheme.color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Radio button + "추천" 배지
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .border(
                                width = 1.dp,
                                color = if (isSelected) ForDayTheme.color.Primary001 else ForDayTheme.color.Border,
                                shape = CircleShape
                            )
                            .background(
                                color = if (isSelected) ForDayTheme.color.Primary001 else ForDayTheme.color.White,
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_check),
                            contentDescription = if (isSelected) "선택됨" else "선택 안됨",
                            modifier = Modifier.size(10.dp),
                            tint = if (isSelected) ForDayTheme.color.White else Color(0xFFD1D1D1)
                        )
                    }

                    if (option.mode == JourneyMode.FORDAY_66) {
                        Text(
                            text = "추천",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Normal,
                            color = ForDayTheme.color.Primary001,
                            lineHeight = 14.sp
                        )
                    }
                }

                // Text content
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = option.title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = ForDayTheme.color.Neutral900,
                        lineHeight = 19.sp
                    )
                    Text(
                        text = option.description,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal,
                        color = ForDayTheme.color.Gray500,
                        lineHeight = 16.8.sp
                    )
                    Spacer(Modifier.height(37.dp))
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SelectJourneyDaysScreenPreview() {
    ForDayTheme {
        SelectJourneyDaysScreen(
            params = null,
            mode = ScreenMode.ONBOARDING,
            hobbyName = "독서",
            hobbyInfoId = 3, // 예시: 독서는 id 3
            selectedTime = "30분",
            selectedFrequency = 2,
            selectedJourneyMode = null,
            onBack = {},
            onNext = {},
            onJourneyModeSelect = {}
        )
    }
}