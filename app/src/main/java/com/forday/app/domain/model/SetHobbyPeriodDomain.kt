package com.forday.app.domain.model

data class SetHobbyPeriodDomain(
    val status: Int,
    val isSuccess: Boolean,
    val data: SetHobbyPeriodDataDomain
)

data class SetHobbyPeriodDataDomain(
    val hobbyId: Int,
    val type: String,
    val message: String,
    val errorClassName: String?
)