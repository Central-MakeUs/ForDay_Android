package com.forday.app.core.session

data class SessionState(
    val accessToken: String? = null,
    val isOnboardingCompleted: Boolean = false,
    val isNicknameSet: Boolean = false,
    val hasSeenIntro: Boolean = false,
    val socialType: String? = null,
    val guestUserId: String? = null,
    val isInitialized: Boolean = false,
    val isLoginSuccess: Boolean = false,
    val isNewUser: Boolean? = null,
    val isTransitioning: Boolean = false,
)
