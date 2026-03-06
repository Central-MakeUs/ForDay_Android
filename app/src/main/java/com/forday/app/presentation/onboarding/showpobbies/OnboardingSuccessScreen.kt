package com.forday.app.presentation.onboarding.showpobbies

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.airbnb.lottie.compose.*
import com.dayn.forday.R
import com.forday.app.core.designsystem.theme.ForDayTheme
import com.forday.app.presentation.onboarding.OnboardingViewModel
import kotlinx.coroutines.delay
import timber.log.Timber

@Composable
fun OnboardingSuccessScreen(
    onNext: () -> Unit,
    onDirectHome: () -> Unit,
    hobbyName: String = "독서",
    viewModel: OnboardingViewModel,
) {
    var isAnimationComplete by remember { mutableStateOf(false) }
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    // 애니메이션 완료 후 자동으로 다음 화면으로 이동
    LaunchedEffect(isAnimationComplete) {
        if (isAnimationComplete) {
            delay(500) // 0.5초 대기
            Timber.e("@@@@@@@@@@@111"+state.isNicknameSet)
//            viewModel.getOnboardingData(

//            else {
                Timber.e("@@@@@@@@@@@222")
                onNext()
//            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ForDayTheme.color.White),
        contentAlignment = Alignment.Center // Box를 중앙 정렬
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(70.dp))

            // 타이틀
            Text(
                text = "주신 정보 너무 감사해요!",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = ForDayTheme.color.Neutral900,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(10.dp))

            // 설명 텍스트
            Text(
                text = "해당 정보들은 포데이 AI 추천에 잘 사용할게요.\n뉴 포비님의 취미생활이 더욱 즐겁도록 함께해요.",
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color = ForDayTheme.color.Gray800,
                textAlign = TextAlign.Center,
                lineHeight = 22.4.sp
            )

            // Lottie 애니메이션
            PobbiesLottieAnimation(
                onAnimationComplete = { isAnimationComplete = true }
            )

            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
fun PobbiesLottieAnimation(
    onAnimationComplete: () -> Unit
) {
    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.onboarding_success) // JSON 파일명
    )

    val animatable = rememberLottieAnimatable()

    LaunchedEffect(composition) {
        composition?.let {
            animatable.animate(
                composition = it,
                iterations = 1 // 1번만 재생
            )
            // 애니메이션 완료 콜백
            onAnimationComplete()
        }
    }

    if (composition != null) {
        LottieAnimation(
            composition = composition,
            progress = { animatable.progress },
            modifier = Modifier
                .fillMaxWidth()
        )
    } else {
        // 로딩 중 플레이스홀더
        Box(
            modifier = Modifier
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "...",
                fontSize = 24.sp,
                color = ForDayTheme.color.Gray400
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 760)
@Composable
private fun OnboardingSuccessScreenPreview() {
    ForDayTheme {
//        ShowPobbiesScreen(
//            onNext = {},
//        )
    }
}
