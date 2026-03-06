package com.forday.app.presentation.main

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.forday.app.core.designsystem.theme.ForDayTheme
import com.forday.app.presentation.onboarding.OnboardingViewModel
import com.forday.app.presentation.onboarding.splash.SplashViewModel
import timber.log.Timber

@Composable
fun AppEntryPoint(
    onboardingViewModel: OnboardingViewModel,
    splashViewModel: SplashViewModel
) {
    val uiState by onboardingViewModel.uiState.collectAsStateWithLifecycle()
    val splashState by splashViewModel.uiState.collectAsStateWithLifecycle()

    // OnboardingViewModel의 initialRoute가 결정되면 SplashViewModel에 전달.
    // SplashViewModel은 API 응답과 realRoute가 모두 준비된 시점에 effectiveRoute를 계산.
    LaunchedEffect(uiState.initialRoute) {
        val route = uiState.initialRoute ?: return@LaunchedEffect
        splashViewModel.setRealRoute(route)
    }

    ForDayTheme {
        splashState.effectiveRoute?.let { route ->
            Timber.e("@@@@@@@@@ effectiveRoute: $route")
            // key(route): effectiveRoute가 변경되면 MainFlow를 완전히 재시작.
            // → Splash에서 realRoute로 변경 시 Splash가 새 백스택에 존재하지 않음.
            key(route) {
                MainFlow(
                    initialRoute = route,
                    onboardingViewModel = onboardingViewModel,
                    splashViewModel = splashViewModel
                )
            }
        }
    }
}
