package com.forday.app.presentation.allsettings

sealed interface SettingsAction {
    data object Logout : SettingsAction
    data object CancelAccount : SettingsAction
}
