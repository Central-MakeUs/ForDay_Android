package com.forday.app.domain.model

data class SwitchAccountDomain(
    val socialType: String,
    val accessToken: String,
    val refreshToken: String
)