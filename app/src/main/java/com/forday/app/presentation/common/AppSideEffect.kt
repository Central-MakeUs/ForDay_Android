package com.forday.app.presentation.common

sealed interface AppSideEffect {
    data class ShowSnackbar(val message: String) : AppSideEffect
    data object NavigateToLogin : AppSideEffect
}
