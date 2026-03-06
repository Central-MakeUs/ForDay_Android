package com.forday.app.presentation.inputhobbyroutines.screen

import com.forday.app.core.logger.analytics.AnalyticsEvents
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.ui.geometry.Offset
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import com.forday.app.core.designsystem.component.clickable.rememberThrottledClick
import com.forday.app.core.designsystem.component.clickable.NoRippleInteractionSource
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.forday.app.core.designsystem.theme.ForDayTheme
import com.dayn.forday.R
import com.forday.app.core.designsystem.component.state.ErrorContent
import com.forday.app.core.designsystem.component.state.ErrorDataUiState
import com.forday.app.presentation.inputhobbyroutines.AiRoutineItemState
import com.forday.app.presentation.inputhobbyroutines.InputRoutinesAndAiRecommendViewModel
import timber.log.Timber

data class RecommendationData(
    val title: String,
    val routines: List<RoutineItem>
)

@Composable
fun AIRecommendationRoutinesScreenRoot(
    hobbyId: Long?,
    onBackClick: () -> Unit,
    onNextClick: () -> Unit,
    viewModel: InputRoutinesAndAiRecommendViewModel
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    state.aiCallCount
    viewModel.logEvent(AnalyticsEvents.AI_RECOMMEND_SCREEN)

    LaunchedEffect(Unit) {
        viewModel.getAiRecommendedRoutines(hobbyId)
    }

    LaunchedEffect(state.aiRoutineList.size) {
        if (state.aiRoutineList.size == 3) {
            viewModel.saveAiRoutines(state.aiRoutineList)
            viewModel.logEvent(AnalyticsEvents.aiRecommendationShown(state.selectedHobbyName, state.aiCallCount))
        }
        Timber.d("@@@ Root에서 감지된 루틴 변경: ${state.aiRoutineList.size}개")
        Timber.d("@@@ 루틴 내용: ${state.aiRoutineList.map { it.content }}")
    }

    val errorData = state.errorData
    if (errorData != null) {
        ErrorContent(
            errorData = errorData,
            onAction = {
                when (errorData.errorType) {
                    ErrorDataUiState.ErrorType.TYPE_RETRY ->
                        viewModel.getAiRecommendedRoutines(hobbyId)
                    ErrorDataUiState.ErrorType.TYPE_BACK ->
                        onBackClick()
                }
            }
        )
    } else {
        AIRecommendationRoutinesScreen(
            routineTitle = state.recommendedText,
            routines = state.aiRoutineList,
            isLoading = state.isLoading,
            onBackClick = {
                viewModel.logEvent(AnalyticsEvents.AI_RECOMMEND_BACK)
                onBackClick()
            },
            onNextClick = { selectedRoutine ->
                viewModel.logEvent(AnalyticsEvents.aiRecommendSelectedRoutine(selectedRoutine.content))
                viewModel.setSelectedAiRoutine(selectedRoutine)
                onNextClick()
            },
            hobbyId = hobbyId,
            viewModel = viewModel
        )
    }

}

@Composable
fun AIRecommendationRoutinesScreen(
    viewModel: InputRoutinesAndAiRecommendViewModel,
    routineTitle: String?,
    routines: List<AiRoutineItemState>,
    isLoading: Boolean,
    onBackClick: () -> Unit,
    hobbyId: Long?,
    onNextClick: (AiRoutineItemState) -> Unit,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val apiCallCount = state.aiCallCount

    val maxApiCalls = 3

    var selectedRoutineIndex by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(routines.size, routines.hashCode()) {
        Timber.d("@@@ UI 렌더링: ${routines.size}개 루틴")
        Timber.d("@@@ 루틴 내용: ${routines.map { it.content }}")
        Timber.d("@@@ API 호출 횟수: $apiCallCount")
        selectedRoutineIndex = null
    }

    val hasSelection = selectedRoutineIndex != null
    val maxReached = apiCallCount >= maxApiCalls

    Timber.d("상태: apiCallCount=$apiCallCount, maxReached=$maxReached")

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ForDayTheme.color.Neutral50)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Header(onBackClick = onBackClick)

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(top = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (isLoading) {
                    AiRecommendTitleSkeleton()
                    Spacer(modifier = Modifier.height(40.dp))
                    SkeletonRoutineList()
                } else {
                    AITitleSection(routineTitle = routineTitle)
                    Spacer(modifier = Modifier.height(40.dp))
                    Column(
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        routines.forEachIndexed { index, routine ->
                            RoutineCard(
                                routine = routine,
                                isSelected = selectedRoutineIndex == index,
                                onSelect = {
                                    selectedRoutineIndex = index
                                    viewModel.logEvent(AnalyticsEvents.aiRecommendationClicked(state.selectedHobbyName, routine.content, index))
                                }
                            )
                        }
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(88.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0x00F9F9F9),
                                Color(0xFFF9F9F9)
                            )
                        )
                    )
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CounterButton(
                    selectedCount = apiCallCount,
                    totalCount = maxApiCalls,
                    isMaxReached = maxReached,
                    onClick = {
                        Timber.d("🔘 CounterButton 클릭: 현재 count=$apiCallCount")
                        viewModel.logEvent(AnalyticsEvents.aiRetryClick(apiCallCount))

                        if (apiCallCount < maxApiCalls) {
                            Timber.d("🔘 API 호출 요청")
                            viewModel.getAiRecommendedRoutines(hobbyId)
                        } else {
                            Timber.d("🔘 최대 호출 횟수 도달")
                        }
                    }
                )

                NextButton(
                    enabled = hasSelection && !isLoading,
                    onClick = {
                        viewModel.logEvent(AnalyticsEvents.CLICK_AI_RECOMMENDATION_NEXT)
                        selectedRoutineIndex?.let { index ->
                            val selectedRoutine = routines[index]
                            onNextClick(selectedRoutine)
                        }
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

// ─── Shimmer Skeleton ────────────────────────────────────────────────────────

// 피그마 모션 스펙: Left to Right / 1.8s / LinearEasing / Infinite / 15°
@Composable
private fun Modifier.shimmerEffect(): Modifier {
    val shimmerColors = listOf(
        Color(0xFFF9F9F9),
        Color(0xFFF2F2F2),
        Color(0xFFF9F9F9)
    )
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_translate"
    )
    val offsetY = (1000f * Math.tan(Math.toRadians(15.0))).toFloat()
    return this.background(
        brush = Brush.linearGradient(
            colors = shimmerColors,
            start = Offset(translateAnim - 1000f, -offsetY),
            end = Offset(translateAnim, offsetY)
        )
    )
}

@Composable
private fun AiRecommendTitleSkeleton() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(4.dp))
                .shimmerEffect()
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .height(20.dp)
                .clip(RoundedCornerShape(4.dp))
                .shimmerEffect()
        )
    }
}

// ✅ 스켈레톤 UI 컴포저블
@Composable
private fun SkeletonRoutineList() {
    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        repeat(3) {
            SkeletonRoutineCard()
        }
    }
}

@Composable
private fun SkeletonRoutineCard() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(4.dp))
            .shimmerEffect()
            .height(80.dp)
    )
}

@Composable
private fun Header(onBackClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(44.dp)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_arrow_back),
            contentDescription = "Back",
            tint = ForDayTheme.color.Gray800,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 23.dp)
                .size(24.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onBackClick
                )
        )

        Text(
            text = "AI 추천 활동",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = ForDayTheme.color.Gray800,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
private fun AITitleSection(routineTitle: String?) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier.size(42.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_ai),
                contentDescription = "AI",
                tint = Color.Unspecified,
                modifier = Modifier.fillMaxSize()
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "$routineTitle",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = ForDayTheme.color.Neutral900,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun RoutineCard(
    routine: AiRoutineItemState,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,  // 리플 효과 제거
                onClick = rememberThrottledClick { onSelect() }
            ),
        shape = RoundedCornerShape(12.dp),
        shadowElevation = 4.dp,
        color = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = routine.content,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = ForDayTheme.color.Neutral900,
                    modifier = Modifier.weight(1f)
                )

                ToggleCheckbox(
                    isChecked = isSelected,
                    onClick = onSelect
                )
            }

            Text(
                text = routine.description,
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color = ForDayTheme.color.Neutral600,
                lineHeight = 19.6.sp
            )
        }
    }
}



@Composable
private fun ToggleCheckbox(
    isChecked: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(20.dp)
            .clip(CircleShape)
            .background(
                color = if (isChecked) ForDayTheme.color.Orange01 else Color.White
            )
            .border(
                width = if (isChecked) 0.dp else 1.dp,
                color = ForDayTheme.color.Gray03,
                shape = CircleShape
            )
            .clickable(onClick = rememberThrottledClick { onClick() }),
        contentAlignment = Alignment.Center
    ) {
        if (isChecked) {
            Icon(
                painter = painterResource(id = R.drawable.ic_check),
                contentDescription = "Checked",
                tint = Color.White,
                modifier = Modifier.size(12.dp)
            )
        }
    }
}

@Composable
private fun CounterButton(
    selectedCount: Int,
    totalCount: Int,
    isMaxReached: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = {
            onClick()
        },
        modifier = Modifier
            .widthIn(min = 56.dp)
            .height(56.dp),
        enabled = !isMaxReached,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.White,
            disabledContainerColor = Color(0xFFF5F5F5)
        ),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(
            width = 1.dp,
            color = if (isMaxReached) Color(0xFFE5E5E5) else Color(0xFFD1D1D1)
        ),
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
                tint = if (!isMaxReached) Color(0xFF3A3A3A) else Color(0xFFB5B5B5)
            )

            Text(
                text = "$selectedCount/$totalCount",
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal,
                color = if (!isMaxReached) Color(0xFF7A7A7A) else Color(0xFFB5B5B5),
                lineHeight = 16.8.sp,
                maxLines = 1,
                softWrap = false
            )
        }
    }
}

@Composable
private fun NextButton(
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.height(56.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = ForDayTheme.color.Orange01,
            contentColor = Color.White,
            disabledContainerColor = ForDayTheme.color.Gray03,
            disabledContentColor = Color.White
        )
    ) {
        Text(
            text = "다음",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
    }
}


data class RoutineItem(
    val id: Long?,
    val title: String,
    val description: String,
    val isCompleted: Boolean
)