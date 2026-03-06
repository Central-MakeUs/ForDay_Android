package com.forday.app.core.designsystem.component.navigationbar

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.forday.app.core.designsystem.theme.ForDayTheme
import com.dayn.forday.R

enum class BottomBarTab {
    HOME,
    DISCOVERY,
    STORY,
    MYPAGE
}

@Composable
fun BottomBar(
    selectedTab: BottomBarTab = BottomBarTab.HOME,
    onTabSelected: (BottomBarTab) -> Unit = {},
    onRecordClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    // 색상 정의
    val neutralWhite = Color(0xFFFFFFFF)
    val neutral900 = Color(0xFF1E1E1E)
    val neutral400 = Color(0xFFB5B5B5)
    val stroke001 = Color(0xFFE5E5E5)
    val gray005 = Color(0xFFE7E7E7)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(neutralWhite)
            .border(1.dp, stroke001)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
        // 홈 탭
        BottomBarIconItem(
            iconRes = if (selectedTab == BottomBarTab.HOME) R.drawable.home_selected else R.drawable.home,
            label = "홈",
            isSelected = selectedTab == BottomBarTab.HOME,
            neutral900 = neutral900,
            neutral400 = neutral400,
            onClick = { onTabSelected(BottomBarTab.HOME) }
        )

        // 발견 탭
        BottomBarIconItem(
            iconRes = if (selectedTab == BottomBarTab.DISCOVERY) R.drawable.discovery_selected else R.drawable.discovery,
            label = "발견",
            isSelected = selectedTab == BottomBarTab.DISCOVERY,
            neutral900 = neutral900,
            neutral400 = neutral400,
            iconSize = 17.dp,
            iconHeight = 23.375.dp,
            onClick = { onTabSelected(BottomBarTab.DISCOVERY) }
        )

        // 가운데 플러스 버튼
        Box(
            modifier = Modifier
                .size(60.dp, 44.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onRecordClick
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_logo_gnb),
                contentDescription = "기록하기",
                modifier = Modifier.fillMaxSize(),
                tint = Color.Unspecified
            )

        }

        // 소식 탭
        Column(
            modifier = Modifier
                .width(60.dp)
                .wrapContentHeight()
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() },
                    onClick = { onTabSelected(BottomBarTab.STORY) }
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(4.dp))

            // 스토리 아이콘
            Box(
                modifier = Modifier.size(20.dp, 24.dp)
            ) {
                if (selectedTab == BottomBarTab.STORY) {
                    // 선택된 상태
                    Box(
                        modifier = Modifier
                            .offset(x = 0.dp, y = 0.84.dp)
                            .size(14.941.dp, 17.929.dp)
                            .clip(RoundedCornerShape(2.988.dp))
                            .background(neutral900)
                            .border(0.747.dp, neutralWhite, RoundedCornerShape(2.988.dp))
                    )
                    Box(
                        modifier = Modifier
                            .offset(x = 5.98.dp, y = 5.07.dp)
                            .size(14.941.dp, 17.929.dp)
                            .clip(RoundedCornerShape(2.988.dp))
                            .background(neutral900)
                            .border(0.747.dp, neutralWhite, RoundedCornerShape(2.988.dp))
                    )
                } else {
                    // 기본 상태
                    Box(
                        modifier = Modifier
                            .offset(x = 0.dp, y = 0.dp)
                            .size(14.941.dp, 17.929.dp)
                            .clip(RoundedCornerShape(2.988.dp))
                            .background(gray005)
                            .border(0.747.dp, neutralWhite, RoundedCornerShape(2.988.dp))
                    )
                    Box(
                        modifier = Modifier
                            .offset(x = 5.98.dp, y = 5.23.dp)
                            .size(14.941.dp, 17.929.dp)
                            .clip(RoundedCornerShape(2.988.dp))
                            .background(gray005)
                            .border(0.747.dp, neutralWhite, RoundedCornerShape(2.988.dp))
                    )
                }
            }

            Spacer(modifier = Modifier.height(3.dp))

            Text(
                text = "소식",
                fontSize = 10.sp,
                fontWeight = FontWeight.Normal,
                color = if (selectedTab == BottomBarTab.STORY) neutral900 else neutral400,
                textAlign = TextAlign.Center,
                lineHeight = 14.sp
            )
        }

        // 마이 탭
        BottomBarIconItem(
            iconRes = if (selectedTab == BottomBarTab.MYPAGE) R.drawable.ic_my_selected else R.drawable.ic_mypage_unselected,
            label = "마이",
            isSelected = selectedTab == BottomBarTab.MYPAGE,
            neutral900 = neutral900,
            neutral400 = neutral400,
            onClick = { onTabSelected(BottomBarTab.MYPAGE) }
        )
    }
    Spacer(modifier = Modifier.navigationBarsPadding())
    }
}

@Composable
private fun BottomBarIconItem(
    iconRes: Int,
    label: String,
    isSelected: Boolean,
    neutral900: Color,
    neutral400: Color,
    onClick: () -> Unit = {},
    iconSize: androidx.compose.ui.unit.Dp = 24.dp,
    iconHeight: androidx.compose.ui.unit.Dp? = null
) {
    Column(
        modifier = Modifier
            .width(60.dp)
            .wrapContentHeight()
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(4.dp))

        Image(
            painter = painterResource(id = iconRes),
            contentDescription = label,
            modifier = if (iconHeight != null) {
                Modifier.size(iconSize, iconHeight)
            } else {
                Modifier.size(iconSize)
            }
        )

        Spacer(modifier = Modifier.height(3.dp))

        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = FontWeight.Normal,
            color = if (isSelected) neutral900 else neutral400,
            textAlign = TextAlign.Center,
            lineHeight = 14.sp
        )
    }
}