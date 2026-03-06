package com.forday.app.core.designsystem.component.layout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun ForDayScreenWrapper(
    backgroundColor: Color = Color.White,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    // 배경층: fillMaxSize + background → 상태바/네비게이션바 영역까지 배경색으로 채움
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        // 콘텐츠층: 시스템 바와 겹치지 않도록 inset 패딩 적용
        Box(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            content()
        }
    }
}
