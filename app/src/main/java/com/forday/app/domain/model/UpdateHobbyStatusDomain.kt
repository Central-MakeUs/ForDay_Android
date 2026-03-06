package com.forday.app.domain.model

data class UpdateHobbyStatusDomain(
    val status: Int,
    val isSuccess: Boolean,
    val data: UpdateHobbyStatusDataDomain
)

data class UpdateHobbyStatusDataDomain(
    val message: String,
    val errorClassName: String?
)