package com.forday.app.domain.model

data class RegisterNicknameDomain(
    val status: Int,
    val isSuccess: Boolean,
    val data: RegisterNicknameDataDomain
)

data class RegisterNicknameDataDomain(
    val nickname: String,
    val message: String
)