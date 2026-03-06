package com.forday.app.presentation.onboarding.login

import android.content.Context

sealed interface LoginAction {
    data class LoginWithKakao(val context: Context) : LoginAction
    data object LoginWithGuest : LoginAction
}
