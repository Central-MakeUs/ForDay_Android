package com.forday.app.remote.model.request

import kotlinx.serialization.Serializable

@Serializable
data class DeleteS3ImageRequest(
    val imageUrl: String
)