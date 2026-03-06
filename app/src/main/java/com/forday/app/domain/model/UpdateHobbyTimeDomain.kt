package com.forday.app.domain.model

data class UpdateHobbyTimeDomain(
    val status: Int,
    val isSuccess: Boolean,
    val data: UpdateHobbyTimeDataDomain
)

data class UpdateHobbyTimeDataDomain(
    val message: String,
    val errorClassName: String?
)