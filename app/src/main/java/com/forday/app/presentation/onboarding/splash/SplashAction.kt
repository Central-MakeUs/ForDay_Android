package com.forday.app.presentation.onboarding.splash

import androidx.navigation3.runtime.NavKey

sealed interface SplashAction {
    data class SetRealRoute(val realRoute: NavKey) : SplashAction
    data object Dismiss : SplashAction
}
