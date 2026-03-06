package com.forday.app.remote.model.request

import kotlinx.serialization.Serializable

@Serializable
data class RecreateHobbyRequest(
    val hobbyInfoId: Long?,
    val hobbyName: String?,
    val hobbyPurpose: String?,
    val hobbyTimeMinutes: Int?,
    val executionCount: Int?,
    val durationSet: Boolean?,
)