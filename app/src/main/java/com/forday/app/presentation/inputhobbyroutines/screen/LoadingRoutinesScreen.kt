package com.forday.app.presentation.inputhobbyroutines.screen

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.airbnb.lottie.compose.*
import com.dayn.forday.R
import com.forday.app.core.designsystem.theme.ForDayTheme
import com.forday.app.presentation.inputhobbyroutines.InputRoutinesAndAiRecommendViewModel
import timber.log.Timber

@Composable
fun LoadingRoutinesScreen(
    hobbyId: Long?,
    onNext: (Long?) -> Unit,
    viewModel: InputRoutinesAndAiRecommendViewModel
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    val loadingMessages = listOf(
        "당신의 취향과 패턴을 분석했어요",
        "AI가 꼭 맞는 활동을 찾아줄게요",
        "활동은 추가로 받을 수 있어요"
    )

    var currentMessageIndex by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        viewModel.getUserNickname()
        for (index in loadingMessages.indices) {
            currentMessageIndex = index
            delay(2500)
        }
        delay(100)
        onNext(hobbyId)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ForDayTheme.color.White)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Spacer(modifier = Modifier.weight(1f))

            LottieLoadingAnimation()

            Spacer(modifier = Modifier.height(31.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "${state.nickname}의 취미를 분석 중",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = ForDayTheme.color.Neutral900,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "${state.selectedHobbyName} AI 활동을 생성 중이에요.",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    color = ForDayTheme.color.Gray800,
                    textAlign = TextAlign.Center,
                    lineHeight = 19.6.sp
                )
            }

            Spacer(modifier = Modifier.weight(1f))
        }

        LoadingToast(
            message = loadingMessages[currentMessageIndex]
        )
    }
}

@Composable
fun LottieLoadingAnimation() {
    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.loading_dots)
    )

    val animatable = rememberLottieAnimatable()

    LaunchedEffect(composition) {
        Timber.d("Lottie composition loaded: ${composition != null}")
        composition?.let {
            Timber.d("Starting animation loop")
            animatable.animate(
                composition = it,
                iterations = LottieConstants.IterateForever,
                speed = 1.0f
            )
        }
    }

    LaunchedEffect(animatable.progress) {
        Timber.d("Lottie progress: ${animatable.progress}")
    }

    if (composition != null) {
        LottieAnimation(
            composition = composition,
            progress = { animatable.progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
        )
    } else {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("Loading...", color = Color.Gray)
        }
    }
}

@Composable
fun BoxScope.LoadingToast(message: String) {
    Box(
        modifier = Modifier
            .align(Alignment.BottomCenter)
            .padding(bottom = 50.dp)
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xCC000000))
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            AnimatedContent(
                targetState = message,
                modifier = Modifier.fillMaxWidth(),
                transitionSpec = {
                    slideInHorizontally(
                        animationSpec = tween(durationMillis = 450),
                        initialOffsetX = { it }
                    ) togetherWith slideOutHorizontally(
                        animationSpec = tween(durationMillis = 450),
                        targetOffsetX = { -it }
                    )
                },
                label = "toast_slide"
            ) { currentMessage ->
                Text(
                    text = currentMessage,
                    modifier = Modifier.fillMaxWidth(),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = ForDayTheme.color.White,
                    textAlign = TextAlign.Center,
                    lineHeight = 19.6.sp
                )
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 760)
@Composable
private fun Preview_LoadingRoutinesScreen() {
    ForDayTheme {
        LoadingRoutinesScreen(
            onNext = {},
            hobbyId = 3,
            viewModel = hiltViewModel()
        )
    }
}