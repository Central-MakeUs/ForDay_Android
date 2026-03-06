package com.forday.app.core.designsystem.component.bottomsheet

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.forday.app.core.designsystem.component.clickable.NoRippleInteractionSource
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.dayn.forday.R
import com.forday.app.core.designsystem.component.clickable.rememberThrottledClick
import com.forday.app.core.designsystem.theme.ForDayTheme
import com.forday.app.core.designsystem.toast.ErrorToast
import com.forday.app.presentation.home.model.AiRoutineItemState
import kotlinx.coroutines.delay
import timber.log.Timber

/**
 * AI 추천 BottomSheet 상태
 */
sealed class AiBottomSheetState {
    object Initial : AiBottomSheetState()
    data class Loading(val userName: String, val hobbyName: String) : AiBottomSheetState()
    data class Result(
        val message: String,
        val recommendations: List<RecommendedActivity>,
        val selectedCount: Int = 0,
        val recommendedText: String = ""
    ) : AiBottomSheetState()
}

/**
 * AI 추천 활동 데이터 모델
 */
data class RecommendedActivity(
    val id: Int,
    val title: String,
    val description: String,
    val isSelected: Boolean = false
)

/**
 * AI 추천 BottomSheet
 * Initial → Loading → Result 순서로 화면 전환
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiRecommendationBottomSheet(
    showBottomSheet: Boolean,
    userName: String = "사용자",
    hobbyName: String,
    onDismiss: () -> Unit,
    aiRecommendData: List<AiRoutineItemState>,
    aiRoutineLoaded: Boolean,
    aiCallCount: Int?,
    aiCallRemainingCount: Int?,
    userSummaryText: String = "",
    recommendedText: String = "",
    toastMessage: String?,
    onDismissToast: () -> Unit,
    onToastAction: () -> Unit,
    errorToastMessage: String? = null,
    onDismissErrorToast: () -> Unit = {},
    onAiRecommendButtonClick: () -> Unit,
    onPreviousRecommendClick: () -> Unit,
    onRecommendationsSelected: (List<RecommendedActivity>) -> Unit = {},
    onAiRecommendationShown: () -> Unit = {},
    onAiRecommendationClicked: (activityName: String, position: Int) -> Unit = { _, _ -> }
) {
    var currentState by remember { mutableStateOf<AiBottomSheetState>(AiBottomSheetState.Initial) }
    var recommendations by remember { mutableStateOf<List<RecommendedActivity>>(emptyList()) }
    var isPreviousRecommend by remember { mutableStateOf(false) }
    var isWaitingForPreviousRecommend by remember { mutableStateOf(false) }

    // BottomSheet가 열릴 때마다 Initial 상태로 초기화
    LaunchedEffect(showBottomSheet) {
        if (showBottomSheet) {
            currentState = AiBottomSheetState.Initial
            recommendations = emptyList()
            isPreviousRecommend = false
            isWaitingForPreviousRecommend = false
            onDismissToast()
        }
    }

    // 성공 토스트 자동 숨김 (3초 후) + BottomSheet 닫기
    LaunchedEffect(toastMessage) {
        if (toastMessage != null) {
            delay(3000)
            onDismissToast()
            onDismiss()
        }
    }

    // 에러 토스트 자동 숨김 (3초 후) — BottomSheet는 닫지 않음
    LaunchedEffect(errorToastMessage) {
        if (errorToastMessage != null) {
            delay(3000)
            onDismissErrorToast()
        }
    }

    // aiRoutineLoaded가 토글될 때마다 항상 Result 화면으로 전환
    LaunchedEffect(aiRoutineLoaded) {
        if (aiRecommendData.isNotEmpty() && (currentState is AiBottomSheetState.Loading || isWaitingForPreviousRecommend)) {
            isWaitingForPreviousRecommend = false
            val convertedRecommendations = aiRecommendData.map { item ->
                RecommendedActivity(
                    id = item.routineId,
                    title = item.content,
                    description = item.description,
                    isSelected = false
                )
            }
            recommendations = convertedRecommendations
            currentState = AiBottomSheetState.Result(
                message = userSummaryText,
                recommendations = convertedRecommendations,
                selectedCount = 0,
                recommendedText = recommendedText
            )
            onAiRecommendationShown()
        }
    }

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = onDismiss,
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = Color.White,
            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
            dragHandle = null,
            sheetMaxWidth = Dp.Unspecified
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateContentSize(
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessMedium
                            )
                        )
                ) {
                AnimatedContent(
                    targetState = currentState,
                    transitionSpec = {
                        fadeIn(animationSpec = tween(300)) togetherWith
                                fadeOut(animationSpec = tween(300))
                    },
                    label = "ai_bottomsheet_state"
                ) { state ->
                    when (state) {
                        is AiBottomSheetState.Initial -> {
                            InitialScreen(
                                aiCallRemainingCount = aiCallRemainingCount,
                                onAiRecommendClick = {
                                    isPreviousRecommend = false
                                    currentState = AiBottomSheetState.Loading(
                                        userName = userName,
                                        hobbyName = hobbyName
                                    )
                                    onAiRecommendButtonClick()
                                },
                                onPreviousRecommendClick = {
                                    isPreviousRecommend = true
                                    isWaitingForPreviousRecommend = true
                                    onPreviousRecommendClick()
                                }
                            )
                        }

                        is AiBottomSheetState.Loading -> {
                            LoadingScreen(
                                userName = state.userName,
                                hobbyName = state.hobbyName
                            )
                        }

                        is AiBottomSheetState.Result -> {
                            ResultScreen(
                                message = state.message,
                                recommendations = recommendations,
                                selectedCount = recommendations.count { it.isSelected },
                                aiCallCount = aiCallCount,
                                isPreviousRecommend = isPreviousRecommend,
                                recommendedText = state.recommendedText,
                                onToggleSelection = { activityId ->
                                    val position = recommendations.indexOfFirst { it.id == activityId }
                                    val activityName = recommendations.find { it.id == activityId }?.title ?: ""
                                    onAiRecommendationClicked(activityName, position)
                                    recommendations = recommendations.map { activity ->
                                        activity.copy(isSelected = activity.id == activityId)
                                    }
                                },
                                onTitleChange = { activityId, newTitle ->
                                    recommendations = recommendations.map { activity ->
                                        if (activity.id == activityId) activity.copy(title = newTitle)
                                        else activity
                                    }
                                },
                                onRegenerate = {
                                    // 재생성 버튼 - 다시 로딩 화면으로
                                    isPreviousRecommend = false
                                    currentState = AiBottomSheetState.Loading(
                                        userName = userName,
                                        hobbyName = hobbyName
                                    )
                                    onAiRecommendButtonClick()
                                },
                                onAdd = {
                                    val selectedActivities =
                                        recommendations.filter { it.isSelected }
                                    onRecommendationsSelected(selectedActivities)
                                }
                            )
                        }
                    }
                }
                } // animateContentSize Box
                if (toastMessage != null && currentState is AiBottomSheetState.Result) {
                    ErrorToast(
                        message = toastMessage!!,
                        actionLabel = "이동하기",
                        onActionClick = {
                            onToastAction()
                            onDismissToast()
                            onDismiss()
                        },
                        iconVisible = true,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .offset(y = -(96).dp)
                    )
                }
                if (errorToastMessage != null && currentState is AiBottomSheetState.Result) {
                    ErrorToast(
                        message = errorToastMessage!!,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .offset(y = -(96).dp)
                    )
                }
            } // outer Box
        }
    }
}

private object BottomSheetConstants {
    @Composable
    fun getScreenHeight(): Dp {
        val configuration = LocalConfiguration.current
        val screenWidth = configuration.screenWidthDp.dp

        return when {
            screenWidth < 360.dp -> 220.dp  // 작은 화면
            screenWidth < 600.dp -> 236.dp  // 일반 폰
            else -> 260.dp  // 태블릿
        }
    }
}

/**
 * Initial 화면 - "AI 추천받기" 버튼
 */
@Composable
private fun InitialScreen(
    aiCallRemainingCount: Int?,
    onAiRecommendClick: () -> Unit,
    onPreviousRecommendClick: () -> Unit
) {
    val screenHeight = BottomSheetConstants.getScreenHeight()
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp

    val horizontalPadding = when {
        screenWidth < 360.dp -> 16.dp
        screenWidth < 600.dp -> 20.dp
        else -> 32.dp
    }

    val topPadding = when {
        screenWidth < 360.dp -> 32.dp
        else -> 40.dp
    }

    val contentSpacing = when {
        screenWidth < 360.dp -> 20.dp
        else -> 24.dp
    }

    val isExhausted = aiCallRemainingCount == 0
    val showPreviousButton = aiCallRemainingCount != null && aiCallRemainingCount <= 2

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (showPreviousButton) Modifier.wrapContentHeight()
                else Modifier.height(screenHeight)
            )
            .background(Color.White)
            .padding(
                top = topPadding,
                start = horizontalPadding,
                end = horizontalPadding,
                bottom = if (showPreviousButton) topPadding else 0.dp
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(contentSpacing)
    ) {
        AiIcon()

        // 버튼 그룹: 두 버튼 사이 간격 12dp
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // AI 추천받기 버튼 (횟수 소진 시 텍스트 변경 + 비활성화)
            Button(
                onClick = rememberThrottledClick(onClick = onAiRecommendClick),
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 400.dp)
                    .heightIn(min = 48.dp),
                enabled = !isExhausted,
                interactionSource = remember { NoRippleInteractionSource() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFFF1E6),
                    disabledContainerColor = Color(0xFFE5E5E5)
                ),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(horizontal = 40.dp, vertical = 12.dp)
            ) {
                Text(
                    text = if (isExhausted) "오늘의 AI 추천 횟수를 다 썼어요." else "AI 추천받기",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isExhausted) Color(0xFFB5B5B5) else Color(0xFFFF9447),
                    textAlign = TextAlign.Center
                )
            }

            // 추천 받은 활동리스트 버튼 (aiCallRemainingCount <= 2 일 때만 표시)
            if (showPreviousButton) {
                Button(
                    onClick = rememberThrottledClick(onClick = onPreviousRecommendClick),
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 400.dp)
                        .heightIn(min = 48.dp),
                    interactionSource = remember { NoRippleInteractionSource() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ForDayTheme.color.MediumGray
                    ),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 40.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = "추천 받은 활동리스트",
                        fontSize = 14.sp,
                        lineHeight = 16.8.sp,
                        fontWeight = FontWeight.W700,
                        color = ForDayTheme.color.Gray800,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

/**
 * Loading 화면 - AI 분석 중
 * Figma: 2189-17808
 */
@Composable
private fun LoadingScreen(
    userName: String,
    hobbyName: String
) {
    val loadingMessages = listOf(
        "당신의 취향과 패턴을 분석했어요",
        "AI 가 맞는 활동을 찾아줄게요",
        "활동은 추가로 만들 수 있어요"
    )
    val screenHeight = BottomSheetConstants.getScreenHeight()
    var currentMessageIndex by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        // 메시지 순환
        for (index in loadingMessages.indices) {
            currentMessageIndex = index
            delay(2500)
        }
    }

    // 화면 크기별 패딩 계산
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp

    val horizontalPadding = when {
        screenWidth < 360.dp -> 16.dp
        screenWidth < 600.dp -> 20.dp
        else -> 32.dp
    }

    val verticalPadding = when {
        screenWidth < 360.dp -> 32.dp
        else -> 40.dp
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()  // 고정 높이 대신 컨텐츠에 맞춤
            .heightIn(screenHeight)  // 최소 높이 보장
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(
                    top = verticalPadding,
                    bottom = verticalPadding,
                    start = horizontalPadding,
                    end = horizontalPadding
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // AI 아이콘
            AiIcon()

            // 애니메이션 점들
            LoadingDots()

            // 텍스트
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 400.dp)  // 최대 너비 제한 (태블릿 대응)
                    .padding(horizontal = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "${userName}의 취미를 분석 중",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E1E1E),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = "$hobbyName AI 활동을 생성 중이에요.",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color(0xFF3A3A3A),
                    textAlign = TextAlign.Center,
                    lineHeight = 19.6.sp,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

/**
 * 로딩 토스트 메시지 (Crossfade 애니메이션)
 */
@Composable
private fun LoadingToastMessage(
    message: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .padding(bottom = 16.dp)
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ) {
        Crossfade(
            targetState = message,
            animationSpec = tween(durationMillis = 300),
            label = "loading_toast"
        ) { currentMessage ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(androidx.compose.foundation.shape.RoundedCornerShape(16.dp))
                    .background(Color(0xCC000000))
                    .padding(vertical = 12.dp, horizontal = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = currentMessage,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    lineHeight = 19.6.sp
                )
            }
        }
    }
}

/**
 * Result 화면 - AI 추천 결과
 * Figma: 2189-18267
 */
@Composable
private fun ResultScreen(
    message: String,
    recommendations: List<RecommendedActivity>,
    aiCallCount: Int?,
    selectedCount: Int,
    isPreviousRecommend: Boolean,
    recommendedText: String,
    onToggleSelection: (Int) -> Unit,
    onTitleChange: (Int, String) -> Unit,
    onRegenerate: () -> Unit,
    onAdd: () -> Unit
) {
    Timber.e("@@@@@@@@@@@########## aiCallCount : "+aiCallCount)
    var showTooltip by remember { mutableStateOf(false) }
    var tooltipHeightPx by remember { mutableIntStateOf(0) }
    val focusManager = LocalFocusManager.current
    val cardListScrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .background(Color.White)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                focusManager.clearFocus()
            }
            .padding(top = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // 헤더 섹션
        Column(
            modifier = Modifier.width(320.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // AI 아이콘
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                AiIcon()

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = message,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E1E1E),
                            textAlign = TextAlign.Center,
                            lineHeight = 21.6.sp
                        )
                        Box {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = recommendedText,
                                    modifier = Modifier.weight(1f),
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1E1E1E),
                                    textAlign = TextAlign.Center,
                                    lineHeight = 21.6.sp
                                )

                                if (!isPreviousRecommend) {
                                    Box {
                                        Icon(
                                            painter = painterResource(id = R.drawable.ic_tooltip),
                                            contentDescription = "정보",
                                            modifier = Modifier
                                                .size(16.dp)
                                                .clickable(
                                                    interactionSource = remember { MutableInteractionSource() },
                                                    indication = null
                                                ) {
                                                    showTooltip = !showTooltip
                                                },
                                            tint = Color(0xFFB5B5B5)
                                        )

                                        if (showTooltip) {
                                            Popup(
                                                alignment = Alignment.TopCenter,
                                                offset = IntOffset(
                                                    x = 0,
                                                    y = -tooltipHeightPx
                                                ),
                                                onDismissRequest = { showTooltip = false }
                                            ) {
                                                TooltipBubble(
                                                    text = "사용자의 취미취향과 다른 유저의\n데이터 기반 추천을 기반하여\n선별된 포데이 AI 추천 취미활동입니다.",
                                                    onDismiss = { showTooltip = false },
                                                    onHeightMeasured = { tooltipHeightPx = it }
                                                )
                                            }
                                        }
                                    }
                                }

                            }
                        }
                    }
                }
            }

            // 추천 카드 리스트 (4개 이상이면 스크롤)
            Column(
                modifier = if (recommendations.size >= 4) {
                    Modifier
                        .heightIn(max = 360.dp)
                        .verticalScroll(cardListScrollState)
                        .padding(bottom = 20.dp)
                } else {
                    Modifier
                },
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                recommendations.forEach { activity ->
                    RecommendationCard(
                        activity = activity,
                        onToggle = { onToggleSelection(activity.id) },
                        onTitleChange = { newTitle -> onTitleChange(activity.id, newTitle) }
                    )
                }
            }
        }

        // 하단 버튼 영역
        BottomButtonSection(
            selectedCount = selectedCount,
            aiCallCount = aiCallCount,
            isPreviousRecommend = isPreviousRecommend,
            onRegenerate = onRegenerate,
            onAdd = onAdd
        )
    }
}

/**
 * 검은 말풍선 툴팁
 */
@Composable
private fun TooltipBubble(
    text: String,
    onDismiss: () -> Unit,
    onHeightMeasured: (Int) -> Unit = {}
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .wrapContentWidth()
            .onGloballyPositioned { coordinates ->
                onHeightMeasured(coordinates.size.height)
            }
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onDismiss() }
    ) {
        // 말풍선 본체
        Box(
            modifier = Modifier
                .wrapContentWidth()
                .background(
                    color = Color(0xFF3A3A3A),
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Text(
                text = text,
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal,
                color = Color.White,
                lineHeight = 16.8.sp,
                textAlign = TextAlign.Center
            )
        }

        // 꼬리 (삼각형 - 아래를 가리킴 ▽)
        Canvas(
            modifier = Modifier
                .size(width = 12.dp, height = 6.dp)
        ) {
            val path = Path().apply {
                moveTo(0f, 0f)
                lineTo(size.width, 0f)
                lineTo(size.width / 2, size.height)
                close()
            }
            drawPath(
                path = path,
                color = Color(0xFF3A3A3A)
            )
        }
    }
}

/**
 * AI 아이콘 (공통)
 */
@Composable
private fun AiIcon() {
    Icon(
        painter = painterResource(id = R.drawable.ic_ai),
        contentDescription = "AI",
        modifier = Modifier.size(42.dp),
        tint = Color.Unspecified
    )
}

/**
 * 로딩 애니메이션 점들
 */
@Composable
private fun LoadingDots() {
    val loadingComposition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.loading_dots)
    )
    val loadingProgress by animateLottieCompositionAsState(
        composition = loadingComposition,
        iterations = LottieConstants.IterateForever
    )

    if (loadingComposition != null) {
        LottieAnimation(
            composition = loadingComposition,
            progress = { loadingProgress },
            modifier = Modifier.heightIn(min = 10.dp)
        )
    }
}

@Composable
private fun RecommendationCard(
    activity: RecommendedActivity,
    onToggle: () -> Unit,
    onTitleChange: (String) -> Unit = {}
) {
    var isEditing by remember { mutableStateOf(false) }
    var hasFocused by remember { mutableStateOf(false) }
    var textFieldValue by remember {
        mutableStateOf(TextFieldValue(activity.title))
    }
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    var lastLineRightPx by remember { mutableFloatStateOf(0f) }
    var lastLineTopPx by remember { mutableFloatStateOf(0f) }
    var lastLineBottomPx by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(isEditing) {
        if (isEditing) {
            hasFocused = false
            textFieldValue = textFieldValue.copy(
                selection = TextRange(textFieldValue.text.length)
            )
            focusRequester.requestFocus()
        } else {
            focusManager.clearFocus()
        }
    }

    Surface(
        onClick = {
            if (isEditing) {
                isEditing = false
                focusManager.clearFocus()
            }
            onToggle()
        },
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
        color = Color.White,
        border = if (activity.isSelected) BorderStroke(1.dp, Color(0xFFFF9447)) else BorderStroke(1.dp, Color(0xFFE5E5E5))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 타이틀 영역
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (isEditing) {
                        Layout(
                            content = {
                                BasicTextField(
                                    value = textFieldValue,
                                    onValueChange = { newValue ->
                                        if (newValue.text.length <= 20) {
                                            textFieldValue = newValue
                                            onTitleChange(newValue.text)
                                        }
                                    },
                                    modifier = Modifier
                                        .focusRequester(focusRequester)
                                        .onFocusChanged { state ->
                                            if (state.isFocused) {
                                                hasFocused = true
                                            } else if (hasFocused) {
                                                isEditing = false
                                                hasFocused = false
                                            }
                                        },
                                    textStyle = LocalTextStyle.current.copy(
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color(0xFF1E1E1E),
                                        lineHeight = 19.09.sp
                                    ),
                                    onTextLayout = { layoutResult ->
                                        val lastLine = layoutResult.lineCount - 1
                                        lastLineRightPx = layoutResult.getLineRight(lastLine)
                                        lastLineTopPx = layoutResult.getLineTop(lastLine)
                                        lastLineBottomPx = layoutResult.getLineBottom(lastLine)
                                    },
                                    singleLine = true
                                )
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_edit),
                                    contentDescription = "완료",
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clickable(
                                            interactionSource = remember { MutableInteractionSource() },
                                            indication = null
                                        ) {
                                            isEditing = !isEditing
                                        },
                                    tint = Color(0xFFB5B5B5)
                                )
                            },
                            modifier = Modifier.weight(1f)
                        ) { measurables, constraints ->
                            val iconSizePx = 24.dp.roundToPx()
                            val gap = 4.dp.roundToPx()
                            val fitsInField = lastLineRightPx + gap + iconSizePx <= constraints.maxWidth
                            val textFieldWidth = if (fitsInField) {
                                constraints.maxWidth
                            } else {
                                constraints.maxWidth - gap - iconSizePx
                            }
                            val textFieldPlaceable = measurables[0].measure(
                                constraints.copy(minWidth = 0, maxWidth = textFieldWidth)
                            )
                            val iconPlaceable = measurables[1].measure(
                                Constraints.fixed(iconSizePx, iconSizePx)
                            )
                            val iconX = if (fitsInField) {
                                (lastLineRightPx + gap).toInt().coerceAtLeast(0)
                            } else {
                                constraints.maxWidth - iconSizePx
                            }
                            val iconY = if (lastLineBottomPx > 0f) {
                                ((lastLineTopPx + lastLineBottomPx) / 2f - iconSizePx / 2f)
                                    .toInt()
                                    .coerceAtLeast(0)
                            } else {
                                ((textFieldPlaceable.height - iconSizePx) / 2).coerceAtLeast(0)
                            }
                            layout(constraints.maxWidth, textFieldPlaceable.height) {
                                textFieldPlaceable.place(0, 0)
                                iconPlaceable.place(iconX, iconY)
                            }
                        }
                    } else {
                        Layout(
                            content = {
                                Text(
                                    text = activity.title,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF1E1E1E),
                                    lineHeight = 19.09.sp,
                                    modifier = Modifier.fillMaxWidth(),
                                    onTextLayout = { layoutResult ->
                                        val lastLine = layoutResult.lineCount - 1
                                        lastLineRightPx = layoutResult.getLineRight(lastLine)
                                        lastLineTopPx = layoutResult.getLineTop(lastLine)
                                        lastLineBottomPx = layoutResult.getLineBottom(lastLine)
                                    }
                                )
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_edit),
                                    contentDescription = "수정",
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clickable(
                                            interactionSource = remember { MutableInteractionSource() },
                                            indication = null
                                        ) {
                                            isEditing = !isEditing
                                        },
                                    tint = Color(0xFFB5B5B5)
                                )
                            },
                            modifier = Modifier.weight(1f)
                        ) { measurables, constraints ->
                            val iconSizePx = 24.dp.roundToPx()
                            val gap = 4.dp.roundToPx()
                            val textPlaceable = measurables[0].measure(constraints)
                            val iconPlaceable = measurables[1].measure(
                                Constraints.fixed(iconSizePx, iconSizePx)
                            )
                            val fitsSameLine = lastLineRightPx + gap + iconSizePx <= constraints.maxWidth
                            val iconX = if (fitsSameLine) (lastLineRightPx + gap).toInt() else 0
                            val iconY = if (fitsSameLine) {
                                ((lastLineTopPx + lastLineBottomPx) / 2f - iconSizePx / 2f).toInt()
                            } else {
                                val lineHeight = (lastLineBottomPx - lastLineTopPx).coerceAtLeast(0f)
                                (lastLineBottomPx + (lineHeight - iconSizePx) / 2f).toInt()
                            }
                            val totalHeight = if (fitsSameLine) {
                                textPlaceable.height
                            } else {
                                (iconY + iconSizePx).coerceAtLeast(textPlaceable.height)
                            }
                            layout(constraints.maxWidth, totalHeight) {
                                textPlaceable.place(0, 0)
                                if (lastLineBottomPx > 0f) {
                                    iconPlaceable.place(iconX, iconY.coerceAtLeast(0))
                                } else {
                                    iconPlaceable.place(0, -(iconSizePx * 2))
                                }
                            }
                        }
                    }
                }

                // 토글 버튼
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .border(
                            width = 1.5.dp,
                            color = if (activity.isSelected) Color(0xFFFF9447) else Color(0xFFD1D1D1),
                            shape = CircleShape
                        )
                        .background(
                            color = if (activity.isSelected) Color(0xFFFF9447) else Color.Transparent,
                            shape = CircleShape
                        )
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            if (isEditing) {
                                isEditing = false
                                focusManager.clearFocus()
                            }
                            onToggle()
                        },
                    contentAlignment = Alignment.Center
                ) {
//                    if (activity.isSelected) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_check),
                            contentDescription = "선택됨",
                            modifier = Modifier.size(12.dp),
                            tint = if (activity.isSelected) Color.White else Color(0xFFD1D1D1)
                        )
//                    }
                }
            }

            // 설명
            Text(
                text = activity.description,
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color = Color(0xFF7A7A7A),
                lineHeight = 19.6.sp
            )
        }
    }
}

/**
 * 하단 버튼 섹션
 */
@Composable
private fun BottomButtonSection(
    selectedCount: Int,
    aiCallCount: Int?,
    isPreviousRecommend: Boolean,
    onRegenerate: () -> Unit,
    onAdd: () -> Unit
) {
    val isRegenerateEnabled = (aiCallCount ?: 0) < 3

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(88.dp)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0x00F9F9F9),
                        Color(0xFFF9F9F9)
                    ),
                    startY = 0f,
                    endY = Float.POSITIVE_INFINITY
                )
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // 재생성 버튼 (추천 받은 활동리스트에서 온 경우 숨김)
            if (!isPreviousRecommend) {
                Button(
                    onClick = onRegenerate,
                    modifier = Modifier
                        .size(56.dp),
                    enabled = isRegenerateEnabled,
                    interactionSource = remember { NoRippleInteractionSource() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        disabledContainerColor = Color.White
                    ),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color(0xFFD1D1D1)),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.icon_reload),
                            contentDescription = "재생성",
                            modifier = Modifier.size(20.dp),
                            tint = if (isRegenerateEnabled) Color(0xFF3A3A3A) else Color(0xFFB5B5B5)
                        )
                        Text(
                            text = "${aiCallCount ?: 0}/3",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Normal,
                            color = if (isRegenerateEnabled) Color(0xFF7A7A7A) else Color(0xFFB5B5B5),
                            lineHeight = 16.8.sp
                        )
                    }
                }
            }

            // 추가 버튼
            Button(
                onClick = rememberThrottledClick(onClick = onAdd),
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                interactionSource = remember { NoRippleInteractionSource() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedCount > 0) Color(0xFFFF9447) else Color(0xFFFFE6D1),
                    disabledContainerColor = Color(0xFFFFE6D1)
                ),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                enabled = selectedCount > 0
            ) {
                Text(
                    text = if (isPreviousRecommend) "활동 담기" else "추가",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    lineHeight = 16.8.sp
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun InitialScreenPreview() {
    InitialScreen(
        onAiRecommendClick = {},
        aiCallRemainingCount = TODO(),
        onPreviousRecommendClick = TODO()
    )
}


@Preview(showBackground = true)
@Composable
private fun LoadingScreenPreview() {
    LoadingScreen(userName = "유지2", hobbyName = "독서")
}

@Preview(showBackground = true)
@Composable
private fun ResultScreenPreview() {
    ResultScreen(
        message = "주로 아침독서를 하셨네요!",
        recommendations = listOf(
            RecommendedActivity(1, "책 5 페이지 읽기", "끝이 정해진 독서라서, 시작이 가볍습니다.", false),
            RecommendedActivity(2, "문단 1개 소리내서 읽기", "소리 내어 읽으면 생각보다 마음이 빨리 가라앉아요.", true),
            RecommendedActivity(3, "출근길 독서", "아침 출근길을 알차게 써보는 건 어떠세요?", false)
        ),
        selectedCount = 1,
        onToggleSelection = {},
        onTitleChange = { _, _ -> },
        onRegenerate = {},
        onAdd = {},
        aiCallCount = 0,
        isPreviousRecommend = TODO(),
        recommendedText = TODO(),
    )
}