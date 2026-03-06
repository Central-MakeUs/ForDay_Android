package com.forday.app.remote.model.request

import kotlinx.serialization.Serializable

@Serializable
data class GetUserScrapListRequest(
    val lastScrapId: Long?,
    val size: Long?,
    val userId: String?,
)
