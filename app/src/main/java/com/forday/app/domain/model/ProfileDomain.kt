package com.forday.app.domain.model

data class ProfileDomain(
    val status: Int?,
    val isSuccess: Boolean?,
    val imageUrl: String?,
    val nickname: String?,
    val stickerCount: Int?,
    val message: String?,
    val errorClassName: String?
)