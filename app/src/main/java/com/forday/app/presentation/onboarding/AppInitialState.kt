package com.forday.app.presentation.onboarding

sealed interface AppInitialState {
    data object Login : AppInitialState
    data object Onboarding : AppInitialState
    data object Home : AppInitialState
}