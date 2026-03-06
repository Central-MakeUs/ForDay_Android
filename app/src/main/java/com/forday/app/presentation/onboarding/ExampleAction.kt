package com.forday.app.presentation.onboarding

sealed interface ExampleAction {
    data object OnClose: ExampleAction
}