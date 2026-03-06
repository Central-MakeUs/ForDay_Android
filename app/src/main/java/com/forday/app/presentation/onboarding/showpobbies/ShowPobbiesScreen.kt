package com.forday.app.presentation.onboarding.showpobbies

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.forday.app.core.designsystem.component.button.BottomNextButton
import com.forday.app.core.designsystem.component.clickable.rememberThrottledClick
import com.forday.app.core.designsystem.component.clickable.NoRippleInteractionSource
import com.forday.app.core.designsystem.theme.ForDayTheme
import androidx.annotation.DrawableRes
import com.forday.app.presentation.onboarding.OnboardingViewModel

data class PobyCharacter(
    val gradientColors: List<Color>,
    val borderColor: Color,
    @DrawableRes val imageRes: Int
)

@Composable
fun ShowPobbiesScreen(
    onNext: () -> Unit = {},
    viewModel: OnboardingViewModel
) {

    val characters = listOf(
        PobyCharacter(
            gradientColors = listOf(
                Color(0x054F49EE), // rgba(79, 73, 238, 0.02)
                Color(0x1F4F49EE), // rgba(79, 73, 238, 0.12)
                Color(0x054F49EE)  // rgba(79, 73, 238, 0.02)
            ),
            borderColor = Color(0x334F49EE), // rgba(79, 73, 238, 0.2)
            imageRes = com.dayn.forday.R.drawable.icon
        ),
        PobyCharacter(
            gradientColors = listOf(
                Color(0x05EE9449), // rgba(238, 148, 73, 0.02)
                Color(0x1FEE9449), // rgba(238, 148, 73, 0.12)
                Color(0x05EE9449)  // rgba(238, 148, 73, 0.02)
            ),
            borderColor = Color(0x33EE9449), // rgba(238, 148, 73, 0.2)
            imageRes = com.dayn.forday.R.drawable.icon_one
        ),
        PobyCharacter(
            gradientColors = listOf(
                Color(0x05EED549), // rgba(238, 213, 73, 0.02)
                Color(0x1FEEE049), // rgba(238, 224, 73, 0.12)
                Color(0x05EED549)  // rgba(238, 213, 73, 0.02)
            ),
            borderColor = Color(0x33EEE049), // rgba(238, 224, 73, 0.2)
            imageRes = com.dayn.forday.R.drawable.icon_two
        ),
        PobyCharacter(
            gradientColors = listOf(
                Color(0x0549EE85), // rgba(73, 238, 133, 0.02)
                Color(0x1F49EE85), // rgba(73, 238, 133, 0.12)
                Color(0x0549EE85)  // rgba(73, 238, 133, 0.02)
            ),
            borderColor = Color(0x3349EE85), // rgba(73, 238, 133, 0.2)
            imageRes = com.dayn.forday.R.drawable.icon_three
        )
    )

    val pagerState = rememberPagerState(
        initialPage = 1,
        pageCount = { characters.size }
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ForDayTheme.color.White)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Spacer(modifier = Modifier.height(60.dp))

            // Title and Description
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "그리고.. 포데이를 함께 할 포비들이에요!",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = ForDayTheme.color.Neutral900
                )

                Text(
                    text = "취미 스티커 콜렉션을 포비로 채워보세요.\n뿌듯하실걸요?",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    color = ForDayTheme.color.Gray800,
                    lineHeight = 19.6.sp
                )
            }

            Spacer(modifier = Modifier.height(80.dp))

            // Character Pager
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
            ) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 60.dp),
                    pageSpacing = 20.dp
                ) { page ->
                    CharacterCard(character = characters[page])
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(characters.size) { index ->
                    Box(
                        modifier = Modifier
                            .size(if (index == pagerState.currentPage) 8.dp else 6.dp)
                            .clip(CircleShape)
                            .background(
                                if (index == pagerState.currentPage)
                                    ForDayTheme.color.Orange01
                                else
                                    Color(0xFFE5E5E5)
                            )
                    )

                    if (index < characters.size - 1) {
                        Spacer(modifier = Modifier.width(6.dp))
                    }
                }
            }
        }

        BottomNextButton(
            modifier = Modifier.align(Alignment.BottomCenter),
            text = "포비와 함께 시작하기",
            enabled = true,
            onNext = onNext
        )
    }
}

@Composable
fun CharacterCard(character: PobyCharacter) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp)
            .clip(RoundedCornerShape(20.dp))
            .border(
                width = 1.dp,
                color = character.borderColor,
                shape = RoundedCornerShape(20.dp)
            )
            .background(
                brush = Brush.linearGradient(
                    colors = character.gradientColors,
                    start = androidx.compose.ui.geometry.Offset(0f, 0f),
                    end = androidx.compose.ui.geometry.Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                )
            )
            .blur(0.5.dp),
        contentAlignment = Alignment.Center
    ) {
        // Character illustration (SVG)
        Icon(
            painter = painterResource(id = character.imageRes),
            contentDescription = "캐릭터 이미지",
            modifier = Modifier.size(112.dp, 110.dp),
            tint = Color.Unspecified  // SVG 원본 색상 유지
        )
    }
}

@Composable
fun BoxScope.BottomButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .align(Alignment.BottomCenter)
            .fillMaxWidth()
            .height(88.dp)
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
                        endY = 88.dp.value
                    )
                )
        )

        // Button
        Button(
            onClick = rememberThrottledClick { onClick() },
            interactionSource = remember { NoRippleInteractionSource() },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 16.dp, vertical = 16.dp)
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = ForDayTheme.color.Orange01,
                contentColor = ForDayTheme.color.White
            )
        ) {
            Text(
                text = "포비와 함께 시작하기",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun ShowPobbiesScreenPreview() {
//    ForDayTheme {
//        ShowPobbiesScreen(
//            onNext = {}
//        )
//    }
//}