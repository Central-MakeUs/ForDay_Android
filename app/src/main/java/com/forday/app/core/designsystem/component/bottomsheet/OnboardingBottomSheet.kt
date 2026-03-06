package com.forday.app.core.designsystem.component.bottomsheet

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import com.forday.app.core.designsystem.component.clickable.NoRippleInteractionSource
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dayn.forday.R
import com.forday.app.core.designsystem.component.clickable.rememberThrottledClick
import com.forday.app.core.designsystem.theme.ForDayTheme

// 색상 정의
object OnboardingColors {
    val Neutral900 = Color(0xFF1E1E1E)
    val Neutral800 = Color(0xFF3A3A3A)
    val Neutral50 = Color(0xFFF9F9F9)
    val White = Color(0xFFFFFFFF)
    val Black = Color(0xFF000000)
    val HomeIndicator = Color(0xFF222222)
    val KakaoYellow = Color(0xFFFFDE00)
    val SecondaryRed = Color(0xFFF25F59)
}

@Composable
fun OnboardingBottomSheet(
    onCloseClick: () -> Unit = {},
    onKakaoLoginClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxSize(),
        color = OnboardingColors.White,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header with Close Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
            ) {
                IconButton(
                    onClick = onCloseClick,
                    modifier = Modifier
                        .size(24.dp)
                        .align(Alignment.CenterEnd)
                        .padding(end = 20.dp),
                    interactionSource = remember { NoRippleInteractionSource() }
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "닫기",
                        tint = OnboardingColors.Neutral800
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Main Content
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Welcome Section
                WelcomeSection()

                // Login Buttons Section
                LoginButtonsSection(
                    onKakaoLoginClick = onKakaoLoginClick,
                )
            }

            // Home Indicator
            HomeIndicator()
        }
    }
}

@Composable
fun WelcomeSection() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.width(320.dp)
    ) {
        // Character Icon
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(Color(0xFFFF9447)),  // 주황색 배경
            contentAlignment = Alignment.Center
        ) {
            // 캐릭터 아이콘 - 실제로는 이미지를 사용
            Text(
                text = "😊",
                fontSize = 32.sp
            )
        }

        // Welcome Title
        Text(
            text = "취미 시작이 어려울 때.\n포데이",
            fontSize = 24.sp,
            fontWeight = FontWeight.SemiBold,
            color = OnboardingColors.Neutral900,
            textAlign = TextAlign.Center,
            lineHeight = 28.8.sp  // lineHeight 1.2
        )

        // Subtitle
        Text(
            text = "AI 추천으로 쉽게 시작하는 취미생활",
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal,
            color = OnboardingColors.SecondaryRed,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun LoginButtonsSection(
    onKakaoLoginClick: () -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
    ) {
        // Hint Bubble
        HintBubble(text = "SNS로 가볍게 시작하기!")

        Spacer(modifier = Modifier.height(8.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Kakao Login Button
            KakaoLoginButton(onClick = onKakaoLoginClick)
        }
    }
}

@Composable
fun HintBubble(text: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Bubble
        Surface(
            shape = RoundedCornerShape(21.dp),
            color = OnboardingColors.White,
            shadowElevation = 8.dp,
            modifier = Modifier.shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(21.dp),
                clip = false
            )
        ) {
            Text(
                text = text,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = OnboardingColors.Neutral900,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
            )
        }

        // Arrow (Triangle pointing down)
        Box(
            modifier = Modifier
                .size(10.dp, 4.dp)
                .offset(y = (-1).dp)
        ) {
            // 흰색 삼각형 모양 (CSS의 border trick 대신 Canvas 사용)
            Canvas(
                modifier = Modifier.fillMaxSize()
            ) {
                val path = Path().apply {
                    moveTo(size.width / 2, size.height)  // 아래 중앙
                    lineTo(0f, 0f)  // 왼쪽 상단
                    lineTo(size.width, 0f)  // 오른쪽 상단
                    close()
                }
                drawPath(
                    path = path,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun KakaoLoginButton(onClick: () -> Unit) {
    Button(
        onClick = rememberThrottledClick(onClick = onClick),
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        interactionSource = remember { NoRippleInteractionSource() },
        colors = ButtonDefaults.buttonColors(
            containerColor = OnboardingColors.KakaoYellow
        ),
        shape = RoundedCornerShape(12.dp),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 14.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Kakao Icon
            Icon(
                painter = painterResource(R.drawable.ic_kakao),  // 실제로는 카카오 아이콘 사용
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = OnboardingColors.Neutral900
            )

            Spacer(modifier = Modifier.width(10.dp))

            Text(
                text = "카카오톡으로 시작하기",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = OnboardingColors.Neutral900
            )
        }
    }
}

@Composable
fun HomeIndicator() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(34.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Box(
            modifier = Modifier
                .width(134.dp)
                .height(5.dp)
                .padding(bottom = 8.dp)
                .clip(RoundedCornerShape(100.dp))
                .background(OnboardingColors.HomeIndicator)
        )
    }
}

// Full Screen Version (for standalone screen, not bottom sheet)
@Composable
fun OnboardingScreen(
    modifier: Modifier = Modifier,
    onCloseClick: () -> Unit = {},
    onKakaoLoginClick: () -> Unit = {},
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(OnboardingColors.White)
    ) {
        OnboardingBottomSheet(
            onCloseClick = onCloseClick,
            onKakaoLoginClick = onKakaoLoginClick,
        )
    }
}

// Preview
@Preview
@Composable
fun PreviewOnboardingBottomSheet() {
    ForDayTheme {
        OnboardingBottomSheet(
            onCloseClick = { },
            onKakaoLoginClick = { },
        )
    }
}

@Preview
@Composable
fun PreviewOnboardingScreen() {
    ForDayTheme {
        OnboardingScreen(
            onCloseClick = { },
            onKakaoLoginClick = { },
        )
    }
}

// With Material3 Bottom Sheet
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingBottomSheetDialog(
    onDismissRequest: () -> Unit = {},
    onKakaoLoginClick: () -> Unit = {},
    onAppleLoginClick: () -> Unit = {}
) {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        containerColor = OnboardingColors.White,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        dragHandle = null
    ) {
        OnboardingBottomSheet(
            onCloseClick = onDismissRequest,
            onKakaoLoginClick = onKakaoLoginClick,
            modifier = Modifier.fillMaxHeight(0.8f)
        )
    }
}

@Preview
@Composable
fun PreviewHintBubble() {
    ForDayTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.LightGray),
            contentAlignment = Alignment.Center
        ) {
            HintBubble(text = "SNS로 가볍게 시작하기!")
        }
    }
}