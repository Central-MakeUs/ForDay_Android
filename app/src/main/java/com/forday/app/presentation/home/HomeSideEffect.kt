package com.forday.app.presentation.home

sealed interface HomeSideEffect {
    data class ShowToast(val message: String) : HomeSideEffect
    data class ShowErrorToast(val message: String) : HomeSideEffect
}
