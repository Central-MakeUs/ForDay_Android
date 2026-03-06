package com.forday.app.presentation.onboarding

sealed interface OnboardingAction {
    data object OnClose: OnboardingAction
}