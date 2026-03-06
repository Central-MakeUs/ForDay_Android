package com.forday.app.remote.model.request

import kotlinx.serialization.Serializable

@Serializable
data class PresignedUrlRequest(
    val images: List<ImageRequest>
)

@Serializable
data class ImageRequest(
    val originalFilename: String,
    val contentType: String, // e.g., "image/jpeg"
    val usage: String, // e.g., "ACTIVITY_RECORD"
    val order: Int
)