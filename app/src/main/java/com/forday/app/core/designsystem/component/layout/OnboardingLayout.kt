package com.forday.app.core.designsystem.component.layout

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.forday.app.core.designsystem.component.progressbar.ForDayProgressBar
import com.forday.app.core.designsystem.component.topbar.SubTopAppBar
import com.forday.app.presentation.onboarding.timeselect.ScreenMode

/**
 * 온보딩 화면들에서 공통으로 사용하는 레이아웃
 * - TopBar (제목, 뒤로가기)
 * - ProgressBar
 * - 콘텐츠 영역
 */
@Composable
fun OnboardingLayout(
    title: String,
    currentStep: Int,
    totalSteps: Int = 5,
    mode: ScreenMode = ScreenMode.ONBOARDING,
    onBack: () -> Unit = {},
    content: @Composable () -> Unit
) {
    val progress = currentStep.toFloat() / totalSteps

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        SubTopAppBar(
            title = title,
            mode = mode,
            onBack = onBack
        )

        if (mode == ScreenMode.ONBOARDING) ForDayProgressBar(progress = progress)

        Box(modifier = Modifier.fillMaxSize()) {
            content()
        }
    }
}