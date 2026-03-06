package com.forday.app.presentation.onboarding.login

import androidx.navigation3.runtime.NavKey

sealed interface LoginSideEffect {
    data class NavigateTo(val destination: LoginDestination) : LoginSideEffect
}

sealed interface LoginDestination {
    data object Onboarding : LoginDestination
    data object Home : LoginDestination
    data object Nickname : LoginDestination
}
