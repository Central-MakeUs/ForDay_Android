package com.forday.app.presentation.onboarding

sealed interface OnboardingSideEffect {
    data object GuestAutoReLoginSuccess : OnboardingSideEffect
    data object GuestAutoReLoginFailure : OnboardingSideEffect
}
