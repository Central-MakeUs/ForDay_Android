package com.forday.app.presentation.allsettings

sealed interface SettingsSideEffect {
    data class DomainError(val error: String): SettingsSideEffect
    data class Exception(val error: Throwable): SettingsSideEffect
    data object LoggedOut : SettingsSideEffect
    data object AccountCancelled : SettingsSideEffect
}