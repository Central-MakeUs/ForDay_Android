package com.forday.app.domain.model

data class BlockUserDomain(
    val status: Int,
    val success: Boolean,
    val data: BlockUserDataDomain
)

data class BlockUserDataDomain(
    val message: String,
    val nickname: String
)
