package com.forday.app.remote.model.request

import kotlinx.serialization.Serializable

@Serializable
data class BlockUserRequest(
    val userId: String
)
