package com.forday.app.presentation.onboarding.purposeselect

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dayn.forday.R
import com.forday.app.core.designsystem.component.button.BottomNextButton
import com.forday.app.core.designsystem.component.layout.OnboardingLayout
import com.forday.app.core.designsystem.dialog.HobbyInputDialog
import com.forday.app.core.designsystem.theme.ForDayTheme
import com.forday.app.core.designsystem.component.clickable.rememberThrottledClick
import com.forday.app.core.designsystem.component.clickable.NoRippleInteractionSource
import com.forday.app.core.logger.analytics.AnalyticsEvents
import com.forday.app.presentation.onboarding.OnboardingViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber

data class PurposeOption(
    val title: String,
    val description: String,
    val isRecommended: Boolean = false
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
fun SelectPurposeScreenRoot(
    onNext: () -> Unit,
    onBack: () -> Unit,
    viewModel: OnboardingViewModel,
) {
    viewModel.logEvent(AnalyticsEvents.HOBBY_PURPOSE_SELECTION_SCREEN)
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    Timber.e("@#@##@#@#@# "+state.selectedPurpose+", "+state.selectedHobbyId+", "+state.selectedFrequency)
    val scope = rememberCoroutineScope()
    SelectPurposeScreen(
        hobbyName = state.selectedHobbyName,
        hobbyInfoId = state.selectedHobbyId?.toInt(), // hobbyInfoId 추가
        selectedTime =
            if (state.selectedMinutes == 60 || state.selectedMinutes == 120) "${state.selectedMinutes!!/60}시간"
            else "${state.selectedMinutes}분",
        customPurposeText = state.customPurposeText,
        selectedPurpose = state.selectedPurpose,
        onBack = {
            viewModel.logEvent(AnalyticsEvents.HOBBY_PURPOSE_SELECTION_BACK)
            scope.launch {
                delay(400L)
                onBack()
            }
        },
        onNext = {
            scope.launch {
                delay(400L)
                onNext()
            }
        },
        onShowCustomDialog = {
            viewModel.logEvent(AnalyticsEvents.HOBBY_PURPOSE_CUSTOM_CLICK)
            viewModel.showDialog()
        },
        onDismissCustomDialog = {
            viewModel.dismissDialog()
        },
        onCustomHobbyConfirm = { text ->
            viewModel.logEvent(AnalyticsEvents.userCustomPurpose(text))
            viewModel.confirmCustomPurpose(text)
            onNext()
        },
        onPurposeSelect = { purposes ->
            viewModel.logEvent(AnalyticsEvents.selectedPurpose(purposes.joinToString(", ")))
            viewModel.savePurposes(purposes)
        },
        showCustomHobbyDialog = state.showDialog,
        selectedFrequency = state.selectedFrequency,
    )
}

@Composable
fun SelectPurposeScreen(
    hobbyName: String?,
    hobbyInfoId: Int? = null, // hobbyInfoId 파라미터 추가
    selectedTime: String,
    selectedPurpose: String?,
    customPurposeText: String,
    showCustomHobbyDialog: Boolean,
    onBack: () -> Unit = {},
    onNext: () -> Unit = {},
    onShowCustomDialog: () -> Unit,
    onDismissCustomDialog: () -> Unit,
    onCustomHobbyConfirm: (String) -> Unit,
    onPurposeSelect: (Set<String>) -> Unit = {},
    selectedFrequency: Int?
) {
    // 단일 선택을 위해 Set 대신 단일 String으로 변경
    var selectedPurpose by remember(selectedPurpose) {
        mutableStateOf(
            if (selectedPurpose?.isNotEmpty() ?: "".isNotEmpty()) {
                selectedPurpose?.split(",")?.firstOrNull() ?: ""
            } else {
                ""
            }
        )
    }
    var localCustomPurposeText by remember(customPurposeText) { mutableStateOf(customPurposeText) }

    val purposeOptions = listOf(
        PurposeOption("스트레스 해소", "마음을 편안하게"),
        PurposeOption("성장과 만족감", "성취감을 얻기 위해", isRecommended = true),
        PurposeOption("에너지 회복", "기분과 활력을 되찾기 위해"),
        PurposeOption("삶의 균형 유지", "일 중심이 아닌\n삶과의 균형을 위해")
    )

    // 최소 1개 이상의 목적이 선택되었는지 확인 (PurposeCard 또는 customHobbyText)
    val hasSelectedPurpose = selectedPurpose.isNotEmpty() || localCustomPurposeText.isNotEmpty()

    OnboardingLayout(
        title = "취미 목적",
        currentStep = 3,
        totalSteps = 5,
        onBack = { onBack() }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(ForDayTheme.color.Neutral50)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
                    .padding(top = 8.dp)
            ) {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    PurposeTitleSection(hobbyName = hobbyName)

                    Spacer(modifier = Modifier.height(24.dp))

                    HobbyCard(
                        hobbyName = hobbyName,
                        hobbyInfoId = hobbyInfoId, // hobbyInfoId 전달
                        timeLabel = selectedTime,
                        isSelected = hasSelectedPurpose,
                        selectedFrequency = selectedFrequency
                    )

                    Spacer(modifier = Modifier.height(40.dp))

                    PurposeGrid(
                        options = purposeOptions,
                        selectedPurpose = selectedPurpose,
                        onPurposeToggle = { purpose ->
                            if (selectedPurpose == purpose) {
                                // 이미 선택된 항목을 다시 클릭하면 선택 해제
                                selectedPurpose = ""
                                onPurposeSelect(setOf())
                            } else {
                                // 새로운 항목 선택 (기존 선택은 자동으로 해제됨)
                                localCustomPurposeText = ""  // PurposeCard를 선택하면 customHobbyText 초기화
                                selectedPurpose = purpose
                                onPurposeSelect(setOf(purpose))
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    AddCustomHobbyButton(
                        customHobbyText = localCustomPurposeText,
                        onClick = onShowCustomDialog,
                    )
                }

                BottomNextButton(
                    text = "다음",
                    enabled = hasSelectedPurpose,
                    onNext = onNext,
                    backgroundColor = ForDayTheme.color.Neutral50
                )
            }

            if (showCustomHobbyDialog) {
                HobbyInputDialog(
                    title = "목적 입력",
                    description = "목적을 입력해 주세요.",
                    onDismiss = onDismissCustomDialog,
                    onNext = { inputText ->
                        // customHobbyText를 입력하면 PurposeCard 선택 초기화
                        selectedPurpose = ""
                        localCustomPurposeText = inputText
                        onCustomHobbyConfirm(inputText)
                        onDismissCustomDialog()
                    }
                )
            }
        }
    }
}

@Composable
fun PurposeTitleSection(hobbyName: String?) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = "어떤 목적으로 시작하나요?",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = ForDayTheme.color.Neutral900
        )

        Text(
            text = buildAnnotatedString {
                withStyle(SpanStyle(color = ForDayTheme.color.Secondary003)) {
                    append(hobbyName)
                }
                withStyle(SpanStyle(color = ForDayTheme.color.Gray800)) {
                    append("를 통해 얻고 싶은 것을 선택해주세요.\n")
                    append("목적이 명확하면 동기부여가 더 쉬워져요!")
                }
            },
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal,
            lineHeight = 19.6.sp
        )
    }
}

@Composable
fun HobbyCard(
    hobbyName: String?,
    hobbyInfoId: Int? = null, // hobbyInfoId 파라미터 추가
    timeLabel: String,
    selectedFrequency: Int?,
    isSelected: Boolean
){
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
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon - hobbyInfoId에 따라 동적으로 아이콘 표시
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

            Spacer(modifier = Modifier.width(10.dp))

            // Text Content
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(3.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = timeLabel,
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

            Icon(
                painter = painterResource(
                    id = R.drawable.onoff
                ),
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = if (isSelected) ForDayTheme.color.Orange03 else ForDayTheme.color.Gray03
            )
        }
    }
}

@Composable
fun PurposeGrid(
    options: List<PurposeOption>,
    selectedPurpose: String,
    onPurposeToggle: (String) -> Unit
) {
    // 2x2 Grid
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // First Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            options.take(2).forEach { option ->
                PurposeCard(
                    option = option,
                    isSelected = selectedPurpose == option.title,
                    onClick = { onPurposeToggle(option.title) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Second Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            options.drop(2).take(2).forEach { option ->
                PurposeCard(
                    option = option,
                    isSelected = selectedPurpose == option.title,
                    onClick = { onPurposeToggle(option.title) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun PurposeCard(
    option: PurposeOption,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .size(156.dp)
            .clickable(
                onClick = rememberThrottledClick { onClick() },
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            )
            .then(
                if (isSelected) {
                    Modifier.border(
                        width = 1.dp,
                        color = ForDayTheme.color.Primary001,
                        shape = RoundedCornerShape(16.dp)
                    )
                } else {
                    Modifier
                }
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = ForDayTheme.color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Checkbox
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .border(
                            width = 1.dp,
                            color = if (isSelected) ForDayTheme.color.Primary001 else ForDayTheme.color.Border,
                            shape = RoundedCornerShape(4.dp)
                        )
                        .background(
                            color = if (isSelected) ForDayTheme.color.Primary001 else ForDayTheme.color.White,
                            shape = RoundedCornerShape(4.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_check),
                        contentDescription = if (isSelected) "선택됨" else "선택 안됨",
                        modifier = Modifier.size(16.dp),
                        tint = if (isSelected) ForDayTheme.color.White else ForDayTheme.color.Border
                    )
                }

                // Text content
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = option.title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = ForDayTheme.color.Neutral900
                    )
                    Text(
                        text = option.description,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal,
                        color = ForDayTheme.color.Neutral600,
                        lineHeight = 16.8.sp
                    )
                }
            }

            // Recommended badge
            if (option.isRecommended) {
                Text(
                    text = "추천",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Normal,
                    color = ForDayTheme.color.Primary001,
                    textAlign = TextAlign.Center,
                    lineHeight = 14.sp,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 16.dp, end = 16.dp)
                )
            }
        }
    }
}

@Composable
private fun AddCustomHobbyButton(customHobbyText: String, onClick: () -> Unit) {
    val isCustomHobbySelected = customHobbyText.isNotEmpty()

    Column {
        Spacer(modifier = Modifier.height(14.dp))
        Button(
            onClick = rememberThrottledClick { onClick() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isCustomHobbySelected) {
                    Color(0xFFFFF4E6)
                } else {
                    Color.White
                }
            ),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(
                width = if (isCustomHobbySelected) 2.dp else 1.dp,
                color = if (isCustomHobbySelected) {
                    ForDayTheme.color.Primary001
                } else {
                    ForDayTheme.color.Gray03
                }
            )
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.icon_pen),
                    contentDescription = "취미 목적 입력 버튼",
                    modifier = Modifier.size(20.dp),
                    tint = ForDayTheme.color.Neutral600
                )

                Spacer(modifier = Modifier.width(10.dp))

                Text(
                    text = if (customHobbyText.isEmpty()) {
                        "원하는 목적이 없으신가요?"
                    } else {
                        customHobbyText
                    },
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = ForDayTheme.color.Neutral600,
                )

                if (isCustomHobbySelected) {
                    Spacer(modifier = Modifier.width(10.dp))

                    Icon(
                        painter = painterResource(id = R.drawable.ic_check),
                        contentDescription = "선택됨",
                        modifier = Modifier.width(10.45455.dp).height(5.90909.dp),
                        tint = Color(0xFFEE9449)
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}


@Preview(showBackground = true)
@Composable
fun SelectPurposeScreenPreview() {
    ForDayTheme {
        SelectPurposeScreen(
            onNext = {},
            hobbyName = "독서",
            hobbyInfoId = 3, // 예시: 독서는 id 3
            selectedTime = "30분",
            customPurposeText = "",
            onBack = {},
            onShowCustomDialog = {},
            onDismissCustomDialog = {},
            onCustomHobbyConfirm = {},
            onPurposeSelect = {},
            showCustomHobbyDialog = false,
            selectedPurpose = "",
            selectedFrequency = 2,
        )
    }
}