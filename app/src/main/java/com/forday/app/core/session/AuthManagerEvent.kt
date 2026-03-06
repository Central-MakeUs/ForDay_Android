package com.forday.app.core.session

sealed interface AuthManagerEvent {
    data object SessionExpired : AuthManagerEvent
    data object GuestReLoginFailed : AuthManagerEvent
    data object GuestReLoginSuccess : AuthManagerEvent
}
