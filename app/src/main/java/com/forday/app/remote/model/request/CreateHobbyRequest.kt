package com.forday.app.remote.model.request

import kotlinx.serialization.Serializable

@Serializable
data class CreateHobbyRequest(
    val hobbyInfoId: Long?,
    val hobbyName: String?,
    val hobbyTimeMinutes: Int?,
    val hobbyPurpose: String?,
    val executionCount: Int?,
    val isDurationSet: Boolean?,
)
