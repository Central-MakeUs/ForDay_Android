package com.forday.app.domain.model

data class ProfileImageDomain(
    val status: Int,
    val isSuccess: Boolean,
    val message: String,
    val imageUrl: String,
    val errorClassName: String
)