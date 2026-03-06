package com.forday.app.domain.model

data class PresignedUrlDomain(
    val status: Int,
    val success: Boolean,
    val data: PresignedUrlDataDomain
)

data class PresignedUrlDataDomain(
    val images: List<PresignedUrlItemDomain>
)

data class PresignedUrlItemDomain(
    val uploadUrl: String,
    val fileUrl: String,
    val order: Int
)