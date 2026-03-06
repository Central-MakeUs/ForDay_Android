package com.forday.app.domain.model

data class UpdateHobbyDurationDomain(
    val status: Int,
    val isSuccess: Boolean,
    val data: UpdateHobbyDurationDataDomain
)

data class UpdateHobbyDurationDataDomain(
    val message: String,
    val errorClassName: String?
)