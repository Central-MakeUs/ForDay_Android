package com.forday.app.domain.model

data class IsNicknameDuplicateDomain(
    val status: Int,
    val isSuccess: Boolean,
    val data: IsNicknameDuplicateDataDomain
)

data class IsNicknameDuplicateDataDomain(
    val nickname: String,
    val message: String,
    val available: Boolean
)

