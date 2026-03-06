package com.forday.app.presentation.home

sealed interface HomeSideEffect {
    data class CreateRoutinesSuccess(val message: String) : HomeSideEffect
    data class CreateRoutinesError(val message: String) : HomeSideEffect
}
