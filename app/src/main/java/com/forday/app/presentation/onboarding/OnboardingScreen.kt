package com.forday.app.presentation.onboarding

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

import androidx.navigation3.runtime.NavKey
import com.forday.app.core.designsystem.component.progressbar.ForDayProgressBar
import com.forday.app.core.designsystem.component.topbar.SubTopAppBar
import com.forday.app.core.designsystem.theme.ForDayTheme
import com.forday.app.presentation.onboarding.frequencyselect.navigation.SelectPerWeek
import com.forday.app.presentation.onboarding.hobbyselect.navigation.SelectHobby
import com.forday.app.presentation.onboarding.periodselect.navigation.SelectPeriod
import com.forday.app.presentation.onboarding.purposeselect.navigation.SelectPurpose
import com.forday.app.presentation.onboarding.timeselect.ScreenMode
import com.forday.app.presentation.onboarding.timeselect.navigation.SelectPerTime
import timber.log.Timber

@Composable
fun OnboardingScreen(
    current: NavKey,
    content: @Composable () -> Unit,
) {
    val steps = remember {
        listOf(
            SelectHobby,
            SelectPerTime,
            SelectPurpose,
            SelectPerWeek,
            SelectPeriod,
        )
    }

    val currentIndex = steps.indexOf(current)
    val isOnboardingStep = currentIndex >= 0
    val progress = if (isOnboardingStep) {
        (currentIndex + 1).toFloat() / steps.size
    } else {
        0f
    }

    val title = when (current) {
        SelectHobby -> "취미 선택"
        SelectPerTime -> "취미 시간"
        SelectPurpose -> "취미 목적"
        SelectPerWeek -> "실행 횟수"
        SelectPeriod -> "여정일"
        else -> ""
    }

    ForDayTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize()
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // 온보딩 단계일 때만 TopBar와 ProgressBar 표시
                if (isOnboardingStep) {
                    SubTopAppBar(
                        title = title,
                        mode = ScreenMode.ONBOARDING,
                    )
                    ForDayProgressBar(progress)
                }

                Box(Modifier.fillMaxSize()) {
                    content()
                }
            }
        }
    }
}

@Preview
@Composable
fun CustomProgressBarPreview() {
    ForDayTheme {
        OnboardingScreen(current = SelectHobby, content = {})
    }
}