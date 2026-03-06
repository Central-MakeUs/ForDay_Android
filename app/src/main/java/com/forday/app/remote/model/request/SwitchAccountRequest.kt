package com.forday.app.remote.model.request

import kotlinx.serialization.Serializable

@Serializable
data class SwitchAccountRequest(
    val socialType: String,
    val socialCode: String,
)