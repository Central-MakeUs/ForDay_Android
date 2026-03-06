package com.forday.app.presentation.onboarding.swipeintro

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dayn.forday.R
import com.forday.app.core.designsystem.component.clickable.NoRippleInteractionSource
import com.forday.app.core.designsystem.theme.ForDayTheme

private val introImages = listOf(
    R.drawable.ic_swipe_2,
    R.drawable.ic_swipe_1,
    R.drawable.ic_swipe_3,
    R.drawable.ic_swipe_4,
    R.drawable.ic_swipe_5,
)

private val IntroBackground = Color(0xFFFFF5EE)

@Composable
fun SwipeIntroScreen(
    onNavigateToLogin: () -> Unit
) {
    BackHandler(enabled = true) { }

    val pagerState = rememberPagerState(pageCount = { introImages.size })
    val lastPageIndex = introImages.size - 1
    val coroutineScope = rememberCoroutineScope()

    // 마지막 페이지에서 왼쪽으로 스와이프 감지 (offset > 0 = 앞으로 스와이프)
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPageOffsetFraction }
            .collect { offset ->
                if (pagerState.currentPage == lastPageIndex && offset > 0.3f) {
                    onNavigateToLogin()
                }
            }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(IntroBackground),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) { page ->
            val isLastPage = page == lastPageIndex
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(
                        indication = null,
                        interactionSource = remember { NoRippleInteractionSource() }
                    ) {
                        if (isLastPage) {
                            onNavigateToLogin()
                        } else {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(page + 1)
                            }
                        }
                    }
            ) {
                Image(
                    painter = painterResource(id = introImages[page]),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 30.dp)
                        .padding(top = 20.dp),
                    contentScale = ContentScale.Fit
                )
            }
        }

        Spacer(modifier = Modifier.height(15.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(introImages.size) { index ->
                val isSelected = index == pagerState.currentPage
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(
                            if (isSelected) Color(0xFF7A7A7A) else Color(0xFFE5E5E5)
                        )
                )
            }
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Preview(showBackground = true)
@Composable
private fun SwipeIntroScreenPreview() {
    ForDayTheme {
        SwipeIntroScreen(onNavigateToLogin = {})
    }
}
