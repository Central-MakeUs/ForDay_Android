package com.forday.app.domain.model

data class DeleteS3ImageDomain(
    val status: Int,
    val isSuccess: Boolean,
    val data: DeleteS3ImageDataDomain
)

data class DeleteS3ImageDataDomain(
    val message: String,
    val errorClassName: String?
)