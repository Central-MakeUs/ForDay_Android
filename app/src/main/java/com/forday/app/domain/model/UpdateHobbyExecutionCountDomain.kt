package com.forday.app.domain.model

data class UpdateHobbyExecutionCountDomain(
    val status: Int,
    val isSuccess: Boolean,
    val data: UpdateHobbyExecutionCountDataDomain
)

data class UpdateHobbyExecutionCountDataDomain(
    val message: String,
    val errorClassName: String?
)