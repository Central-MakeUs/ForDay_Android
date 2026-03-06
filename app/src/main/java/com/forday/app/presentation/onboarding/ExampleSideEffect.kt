package com.forday.app.presentation.onboarding

sealed interface ExampleSideEffect {
    data class Error(val error: Throwable): ExampleSideEffect
    data object OnClose: ExampleSideEffect
}