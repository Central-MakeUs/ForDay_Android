package com.forday.app.core.designsystem.component.topbar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import com.forday.app.core.designsystem.component.clickable.NoRippleInteractionSource
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.forday.app.core.designsystem.theme.ForDayTheme
import com.dayn.forday.R
import com.forday.app.presentation.onboarding.timeselect.ScreenMode

/**
 * ForDay 메인 헤더 (Level 1)
 * 페이지명 + 드롭다운 + 설정 + 알림
 *
 * @param title 페이지 제목
 * @param onTitleClick 제목 클릭 이벤트 (드롭다운)
 * @param onSettingsClick 설정 아이콘 클릭
 * @param onNotificationClick 알림 아이콘 클릭
 * @param showNotificationBadge 알림 배지 표시 여부
 * @param modifier Modifier
 */
@Composable
fun MainTopAppBar(
    title: String,
    onTitleClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    showNotificationBadge: Boolean = false,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(54.dp),
        color = Color.White,
        shadowElevation = 0.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            // 왼쪽: 페이지명 + 드롭다운
            Row(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() },
                        onClick = onTitleClick
                    ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = title,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E1E1E),
                    lineHeight = 26.sp
                )

                // 드롭다운 아이콘
                Icon(
                    painter = painterResource(id = R.drawable.ic_chevron_down),
                    contentDescription = "드롭다운",
                    modifier = Modifier.size(24.dp),
                    tint = ForDayTheme.color.Gray800
                )
            }

            // 오른쪽: 설정 + 알림
            Row(
                modifier = Modifier.align(Alignment.CenterEnd),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 설정 아이콘
                IconButton(
                    onClick = onSettingsClick,
                    modifier = Modifier.size(24.dp),
                    interactionSource = remember { NoRippleInteractionSource() }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_settings),
                        contentDescription = "설정",
                        modifier = Modifier.size(24.dp),
                        tint = ForDayTheme.color.Gray800
                    )
                }

                // 알림 아이콘 (배지 포함)
                Box(
                    modifier = Modifier.size(24.dp)
                ) {
                    IconButton(
                        onClick = onNotificationClick,
                        modifier = Modifier.fillMaxSize(),
                        interactionSource = remember { NoRippleInteractionSource() }
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_notification),
                            contentDescription = "알림",
                            modifier = Modifier.size(22.dp),
                            tint = ForDayTheme.color.Gray800
                        )
                    }

                    // 알림 배지
                    if (showNotificationBadge) {
                        Box(
                            modifier = Modifier
                                .size(4.dp)
                                .background(
                                    color = Color(0xFFF4A261),
                                    shape = androidx.compose.foundation.shape.CircleShape
                                )
                                .align(Alignment.TopEnd)
                        )
                    }
                }
            }
        }
    }
}

/**
 * ForDay 서브 헤더 (Level 2)
 * 뒤로가기 + 제목 + 닫기
 *
 * @param title 페이지 제목
 * @param onBack 뒤로가기 클릭
 * @param onCloseClick 닫기 클릭
 * @param showCloseButton 닫기 버튼 표시 여부
 * @param modifier Modifier
 */
@Composable
fun SubTopAppBar(
    title: String,
    mode: ScreenMode = ScreenMode.DEFAULT,
    onBack: () -> Unit = {},  // 뒤로가기 OR 닫기
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(44.dp),
        color = ForDayTheme.color.Neutral50,
        shadowElevation = 0.dp
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // 왼쪽: 뒤로가기 or 닫기
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 20.dp)
                    .size(24.dp),
                interactionSource = remember { NoRippleInteractionSource() }
            ) {
                Icon(
                    painter = painterResource(id = if (mode == ScreenMode.ONBOARDING) R.drawable.ic_arrow_back else R.drawable.ic_close),
                    contentDescription = "뒤로가기",
                    modifier = Modifier.size(24.dp),
                    tint = ForDayTheme.color.Gray800
                )
            }

            Text(
                text = title,
                color = ForDayTheme.color.Gray800,
                modifier = Modifier.align(Alignment.Center),
                style = ForDayTheme.typography.title16
            )

//            if (showCloseButton) {
//                IconButton(
//                    onClick = onCloseClick,
//                    modifier = Modifier
//                        .align(Alignment.CenterEnd)
//                        .padding(end = 20.dp)
//                        .size(24.dp)
//                ) {
//                    Icon(
//                        painter = painterResource(id = R.drawable.ic_close),
//                        contentDescription = "닫기",
//                        modifier = Modifier.size(24.dp),
//                        tint = ForDayTheme.color.Gray800
//                    )
//                }
//            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainTopAppBarPreview() {
    ForDayTheme {
        MainTopAppBar(
            title = "페이지명",
            showNotificationBadge = true
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MainTopAppBarNoBadgePreview() {
    ForDayTheme {
        MainTopAppBar(
            title = "홈",
            showNotificationBadge = false
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SubTopAppBarPreview() {
    ForDayTheme {
        SubTopAppBar(
            title = "페이지명"
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SubTopAppBarNoClosePreview() {
    ForDayTheme {
        SubTopAppBar(
            title = "페이지명",
            mode = ScreenMode.DEFAULT
        )
    }
}