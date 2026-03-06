package com.forday.app.presentation.inputhobbyroutines.screen

import com.forday.app.core.logger.analytics.AnalyticsEvents
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dayn.forday.R
import com.forday.app.core.designsystem.component.button.AIRecommendationButton
import com.forday.app.core.designsystem.component.clickable.rememberThrottledClick
import com.forday.app.core.designsystem.component.clickable.NoRippleInteractionSource
import com.forday.app.core.designsystem.component.state.ErrorContent
import com.forday.app.core.designsystem.component.state.ErrorDataUiState
import com.forday.app.core.designsystem.theme.ForDayTheme
import com.forday.app.core.designsystem.toast.ErrorToast
import com.forday.app.presentation.inputhobbyroutines.AiRoutineItemState
import com.forday.app.presentation.inputhobbyroutines.InputRoutinesAndAiRecommendSideEffect
import com.forday.app.presentation.inputhobbyroutines.InputRoutinesAndAiRecommendViewModel
import com.forday.app.presentation.inputhobbyroutines.RoutinesState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.UUID


data class RoutineInput(
    val id: String = UUID.randomUUID().toString(),
    val text: String = "",
    val isAiRecommended: Boolean = false  // AI 추천 여부 추가
)

@Composable
fun InputRoutineScreenRoot(
    hobbyId: Long?,
    hobbyName: String?,
    aiCallRemaining: Boolean?,
    onAIRecommendationRoutines: (Long?) -> Unit,
    onExit: () -> Unit,
    onNavigateToModifyRoutine: () -> Unit,
    viewModel: InputRoutinesAndAiRecommendViewModel
) {

    var showToast by remember { mutableStateOf(false) }
    var hasAiRoutine by remember { mutableStateOf(false) }
    var aiRecommendationButtonTopY by remember { mutableStateOf<Float?>(null) }
    var toastHeightPx by remember { mutableStateOf(0) }
    val density = LocalDensity.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val state by viewModel.uiState.collectAsStateWithLifecycle()
//    var resetTrigger by remember { mutableStateOf(0) }
    LaunchedEffect(Unit) {
        viewModel.initHobbyName(hobbyName)
        viewModel.searchHobbyMatesRoutines(selectedHobbyId = hobbyId)
    }

    LaunchedEffect(Unit) {
        viewModel.sideEffect.collect { effect ->
            when (effect) {
                is InputRoutinesAndAiRecommendSideEffect.CreateRoutinesSuccess -> {
                    if (hasAiRoutine) showToast = true
                    if (hasAiRoutine) delay(1000L)
                    onNavigateToModifyRoutine()
                    showToast = false
                }
                else -> Unit
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        InputRoutineScreen(
            onAIRecommendationRoutines = {
                onAIRecommendationRoutines(hobbyId)
                viewModel.logEvent(AnalyticsEvents.HOBBY_INPUT_AI_RECOMMENDATIONS)
            },
            onCreateRoutines = { list ->
                hasAiRoutine = list.any { it.first }
                list.forEach { viewModel.logEvent(AnalyticsEvents.finalHobbyActivity(it.toString())) }
                viewModel.logEvent(AnalyticsEvents.CREATE_HOBBY_CLICK)
                viewModel.createRoutines(
                    hobbyId = hobbyId,
                    routineList = list
                )
            },
            hobbymateRoutines = state.hobbymateRoutines,
            hobbyId = hobbyId,
            selectedAiRoutine = state.selectedAiRoutine,
            onClearSelectedAiRoutine = { viewModel.clearSelectedAiRoutine() },
            state = state,
            getHobbyMatesRoutines = { viewModel.searchHobbyMatesRoutines(selectedHobbyId = hobbyId) },
            onExit = { onExit() },
            aiCallRemaining = aiCallRemaining,  // 추가
            viewModel = viewModel,
            onAiRecommendationButtonTopYChanged = { topY ->
                aiRecommendationButtonTopY = topY
            },
//            resetTrigger = resetTrigger
        )

        val toastYOffsetPx = aiRecommendationButtonTopY?.let { topY ->
            val gapPx = with(density) { 8.dp.roundToPx() }
            (topY.toInt() - toastHeightPx - gapPx).coerceAtLeast(0)
        } ?: with(density) { 54.dp.roundToPx() }

        AnimatedVisibility(
            visible = showToast,
            enter = fadeIn(animationSpec = tween(300)),
            exit = fadeOut(animationSpec = tween(300)),
            modifier = Modifier
                .fillMaxWidth()
                .offset { IntOffset(0, toastYOffsetPx) }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier.onGloballyPositioned { coordinates ->
                        toastHeightPx = coordinates.size.height
                    }
                ) {
                    ErrorToast(message = "AI 취미활동을 담았어요.", iconVisible = true)
                }
            }
        }
    }
}

@Composable
fun InputRoutineScreen(
    state: RoutinesState,
    onCreateRoutines: (List<Pair<Boolean, String>>) -> Unit,
    hobbymateRoutines: List<String>,
    onAIRecommendationRoutines: () -> Unit,
    hobbyId: Long?,
    aiCallRemaining: Boolean?,
    selectedAiRoutine: AiRoutineItemState?,
    onClearSelectedAiRoutine: () -> Unit,
    getHobbyMatesRoutines: () -> Unit,
    onExit: () -> Unit,
    resetTrigger: Int = 0,
    viewModel: InputRoutinesAndAiRecommendViewModel,
    onAiRecommendationButtonTopYChanged: (Float) -> Unit = {}
) {
    val routineInputListSaver = Saver<List<RoutineInput>, List<List<Any>>>(
        save = { list ->
            list.map { listOf(it.id, it.text, it.isAiRecommended) }
        },
        restore = { savedList ->
            savedList.map { item ->
                RoutineInput(
                    id = item[0] as String,
                    text = item[1] as String,
                    isAiRecommended = item[2] as Boolean
                )
            }
        }
    )


    val focusManager = LocalFocusManager.current
    val visibilityMapSaver = Saver<Map<String, Boolean>, List<List<Any>>>(
        save = { map ->
            map.entries.map { entry -> listOf(entry.key, entry.value) }
        },
        restore = { savedList ->
            savedList.associate { item ->
                item[0] as String to item[1] as Boolean
            }
        }
    )
    LaunchedEffect(hobbyId) {  // Unit 대신 hobbyId를 key로
        Timber.e("@@@@@@@@@@@@############# " + hobbyId)
        viewModel.resetInputState()  // selectedAiRoutine 초기화
    }
    var activities by rememberSaveable(
        key = "activities_$hobbyId",  // hobbyId가 바뀌면 새로 초기화
        stateSaver = routineInputListSaver
    ) {
        mutableStateOf(listOf(RoutineInput()))
    }
    var recommendationIndex by rememberSaveable {
        mutableStateOf(0)
    }

    var visibleActivities by rememberSaveable(stateSaver = visibilityMapSaver) {
        mutableStateOf(mapOf(activities[0].id to true))
    }

    LaunchedEffect(resetTrigger) {
        if (resetTrigger > 0) {
            val newActivity = RoutineInput()
            activities = listOf(newActivity)
            visibleActivities = mapOf(newActivity.id to true)
            recommendationIndex = 0
            focusManager.clearFocus()  // 포커스 해제
        }
    }

    // AI 추천 루틴 선택 시 마지막 ActivityInputField에 자동 입력
    // activities가 1개면 1개에, 2개면 2번째(마지막)에, 3개면 3번째(마지막)에 입력
    Timber.e("##@@@@@@@@@@@@1  " + selectedAiRoutine?.content)
    LaunchedEffect(selectedAiRoutine) {
        Timber.e("##@@@@@@@@@@@@2  " + selectedAiRoutine?.content)
        selectedAiRoutine?.let { routine ->
            // 현재 activities 개수에 관계없이 항상 마지막(가장 아래) activity에 입력
            val lastActivity = activities.last()
            activities = activities.dropLast(1) + lastActivity.copy(
                text = routine.content,
                isAiRecommended = true  // AI 추천으로 표시
            )
            Timber.e("##@@@@@@@@@@@@3  " + selectedAiRoutine.content)
            // 사용 후 초기화
//            onClearSelectedAiRoutine()
        }
    }

    val hasValidActivities = activities.any { it.text.isNotBlank() }

    val listRoutines = state.hobbymateRoutines

    val currentRecommendations = remember(recommendationIndex, hobbymateRoutines) {
        if (hobbymateRoutines.isEmpty()) {
            emptyList()  // 데이터 없으면 빈 리스트
        } else {
            val startIndex = (recommendationIndex * 3) % hobbymateRoutines.size
            List(3) { i -> hobbymateRoutines[(startIndex + i) % hobbymateRoutines.size] }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ForDayTheme.color.White)
            .clickable(
                onClick = rememberThrottledClick { focusManager.clearFocus() },
                interactionSource = remember { MutableInteractionSource() },
                indication = null  // 리플 효과 제거
            )
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Header(title = "취미활동 입력", onClose = onExit)

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp)
            ) {
                Spacer(modifier = Modifier.height(20.dp))

                // 상단 아이콘과 타이틀 섹션
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(88.dp)
                            .clip(CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_create_routine),
                            contentDescription = "취미활동 생성 화면 아이콘",
                            tint = Color.Unspecified
                        )
                    }

                    Text(
                        text = "하고 싶은 취미활동을 적어주세요.",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = ForDayTheme.color.Neutral900,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    Column(
                        modifier = Modifier.animateContentSize(
                            animationSpec = tween(300)
                        ),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // activities를 순회하며 ActivityInputField 표시
                        // 마지막 항목이 AI 추천을 받을 대상
                        activities.forEach { activity ->
                            key(activity.id) {
                                val coroutineScope = rememberCoroutineScope()
                                AnimatedActivityInputField(
                                    activity = activity,
                                    visible = visibleActivities[activity.id] ?: true,
                                    onTextChange = { newText ->
                                        activities = activities.map {
                                            if (it.id == activity.id) it.copy(
                                                text = newText,
                                                isAiRecommended = false  // 사용자 직접 입력
                                            )
                                            else it
                                        }
                                    },
                                    onDelete = {
                                        visibleActivities =
                                            visibleActivities + (activity.id to false)
                                        coroutineScope.launch {
                                            delay(300)
                                            activities = activities.filter { it.id != activity.id }
                                            visibleActivities = visibleActivities - activity.id
                                        }
                                    },
                                    showDelete = activities.size > 1
                                )
                            }
                        }
                    }

                    if (currentRecommendations.isNotEmpty()) {
                        @OptIn(ExperimentalLayoutApi::class)
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = "나와 같은 취미를 선택한 포비들이 하고 있는 활동이에요.",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = ForDayTheme.color.Neutral600,
                                lineHeight = 16.8.sp
                            )

                            FlowRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                currentRecommendations.forEachIndexed { index, text ->
                                    RecommendationChip(
                                        text = text,
                                        onClick = {
                                            val lastActivity = activities.last()
                                            activities = activities.dropLast(1) + lastActivity.copy(
                                                text = text,
                                                isAiRecommended = false
                                            )
                                        }
                                    )
                                }
                            }
                        }
                    }

                    val canAddMore = activities.size < 3 && activities.last().text.isNotBlank()

                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .border(
                                    width = 1.dp,
                                    color = if (canAddMore) ForDayTheme.color.Gray03 else ForDayTheme.color.Gray03.copy(
                                        alpha = 0.3f
                                    ),
                                    shape = RoundedCornerShape(4.dp)
                                )
                                .clip(RoundedCornerShape(4.dp))
                                .background(
                                    if (canAddMore) ForDayTheme.color.White else ForDayTheme.color.Gray03.copy(
                                        alpha = 0.1f
                                    )
                                )
                                .clickable(
                                    onClick = rememberThrottledClick {
                                        val newActivity = RoutineInput()
                                        activities = activities + newActivity
                                        visibleActivities = visibleActivities + (newActivity.id to true)
                                        recommendationIndex = (recommendationIndex + 1) % 3
                                        getHobbyMatesRoutines()
                                    },
                                    enabled = canAddMore
                                ),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_plus),
                                contentDescription = "활동 추가",
                                tint = if (canAddMore) ForDayTheme.color.Gray800 else ForDayTheme.color.Gray800.copy(
                                    alpha = 0.3f
                                ),
                                modifier = Modifier.size(12.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(180.dp))
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AIRecommendationButton(
                onCreateAiRoutines = onAIRecommendationRoutines,
                enabled = true,
                modifier = Modifier.onGloballyPositioned { coordinates ->
                    onAiRecommendationButtonTopYChanged(coordinates.positionInRoot().y)
                }
            )
            BottomButton2(
                enabled = hasValidActivities,
                onCreateRoutines = {
                    val routineList = activities
                        .filter { it.text.isNotBlank() }
                        .map {
                            Timber.e("@@@@@@@@@@@@@@########### " + it.isAiRecommended + ", " + it.text)
                            Pair(it.isAiRecommended, it.text)
                        }

                    onCreateRoutines(routineList)
                }
            )
        }
    }
}

@Composable
fun AnimatedActivityInputField(
    activity: RoutineInput,
    visible: Boolean,
    onTextChange: (String) -> Unit,
    onDelete: () -> Unit,
    showDelete: Boolean
) {
    AnimatedVisibility(
        visible = visible,
        enter = expandVertically(
            animationSpec = tween(300)
        ) + fadeIn(animationSpec = tween(300)),
        exit = shrinkVertically(
            animationSpec = tween(300)
        ) + fadeOut(animationSpec = tween(300))
    ) {
        ActivityInputField(
            text = activity.text,
            isAiRecommended = activity.isAiRecommended,
            onTextChange = onTextChange,
            onDelete = onDelete,
            showDelete = showDelete
        )
    }
}

@Composable
fun ActivityInputField(
    text: String,
    isAiRecommended: Boolean,
    onTextChange: (String) -> Unit,
    onDelete: () -> Unit,
    showDelete: Boolean
) {
    val bringIntoViewRequester = remember { BringIntoViewRequester() }
    val coroutineScope = rememberCoroutineScope()
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused = interactionSource.collectIsFocusedAsState()

    val maxLength = 20
    val currentLength = text.length
    val placeholder = "예시) 좋아하는 문장 필사하기, 5분 책 읽기, 블로그에 독후감 쓰기"

    LaunchedEffect(isFocused.value) {
        if (isFocused.value) {
            delay(500) // 300 -> 500으로 증가
            bringIntoViewRequester.bringIntoView(
                // 추가 여유 공간 확보
                rect = androidx.compose.ui.geometry.Rect(
                    left = 0f,
                    top = -100f, // 위쪽 여유
                    right = 0f,
                    bottom = 100f // 아래쪽 여유
                )
            )
        }
    }


    Box(
        modifier = Modifier
            .fillMaxWidth()
            .bringIntoViewRequester(bringIntoViewRequester) // 추가
            .heightIn(min = 100.dp)
            .background(Color(0XFFF9F9F9), RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // AI 추천 배지
                    if (isAiRecommended) {
                        AiRecommendChip()
                    }

                    // 입력 필드
                    BasicTextField(
                        value = text,
                        onValueChange = { if (it.length <= maxLength) onTextChange(it) },
                        modifier = Modifier.fillMaxWidth(),
                        interactionSource = interactionSource,
                        textStyle = TextStyle(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Normal,
                            color = if (text.isEmpty()) ForDayTheme.color.Gray500 else ForDayTheme.color.Gray800,
                            lineHeight = 19.6.sp
                        ),
                        decorationBox = { innerTextField ->
                            if (text.isEmpty()) {
                                Text(
                                    text = placeholder,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Normal,
                                    color = ForDayTheme.color.Gray500,
                                    lineHeight = 19.6.sp
                                )
                            }
                            innerTextField()
                        }
                    )
                }

                if (showDelete) {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() },
                            onClick = rememberThrottledClick { onDelete() }
                        ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_trash),
                            contentDescription = "삭제 버튼",
                            tint = ForDayTheme.color.Gray500,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "$currentLength/$maxLength",
                fontSize = 10.sp,
                fontWeight = FontWeight.Normal,
                color = ForDayTheme.color.StrongDivider,
                textAlign = TextAlign.End,
                modifier = Modifier.fillMaxWidth(),
                lineHeight = 14.sp
            )
        }
    }
}

@Composable
fun RecommendationChip(
    text: String,
    onClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(40.dp),
                spotColor = Color.Black.copy(alpha = 0.06f)
            )
            .clip(RoundedCornerShape(40.dp))
            .background(ForDayTheme.color.White)
            .clickable(onClick = rememberThrottledClick { onClick() })
            .padding(horizontal = 8.dp, vertical = 6.dp)
    ) {
        Text(
            text = text,
            fontSize = 12.sp,
            fontWeight = FontWeight.Normal,
            color = ForDayTheme.color.Gray800,
            lineHeight = 16.8.sp
        )
    }
}

@Composable
fun AiRecommendChip(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFFF4A261), // 오렌지
                        Color(0xFFF77F78)  // 핑크
                    ),
                    start = Offset(0f, 0f),
                    end = Offset(1f, 1f)
                ),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(horizontal = 6.dp, vertical = 4.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_ai_btn),
                contentDescription = "AI",
                tint = Color.White,
                modifier = Modifier.size(9.dp)
            )

            Text(
                text = "AI추천",
                fontSize = 10.sp,
                color = Color.White,
                fontWeight = FontWeight.Normal,
                fontFamily = FontFamily(Font(R.font.pretendard_std_variable)),
                lineHeight = 14.sp
            )
        }
    }
}

@Composable
fun Header(title: String, onClose: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(44.dp),
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_close),
            contentDescription = "닫기",
            tint = ForDayTheme.color.Gray800,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 20.dp)
                .size(24.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = rememberThrottledClick { onClose() }
                )
        )
        Text(
            text = title,
            fontSize = 16.sp,
            lineHeight = 19.2.sp,
            fontWeight = FontWeight.Bold,
            color = ForDayTheme.color.Gray800,
            textAlign = TextAlign.Center,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
fun BottomButton2(
    enabled: Boolean,
    onCreateRoutines: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(88.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0f),
                            Color.White
                        )
                    )
                )
        )

        Button(
            onClick = rememberThrottledClick(onClick = onCreateRoutines),
            enabled = enabled,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 16.dp, vertical = 16.dp)
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (enabled) {
                    ForDayTheme.color.Orange01
                } else ForDayTheme.color.Gray03,
                contentColor = ForDayTheme.color.White,
                disabledContainerColor = ForDayTheme.color.Gray03,
                disabledContentColor = ForDayTheme.color.White
            )
        ) {
            Text(
                text = "취미활동 생성",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun InputRoutineScreenPreview() {
    ForDayTheme {
    }
}