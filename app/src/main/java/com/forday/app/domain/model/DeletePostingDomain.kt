package com.forday.app.domain.model

data class DeletePostingDomain(
    val isSuccess: Boolean,
    val message: String,
    val recordId: Int,
    val errorClassName: String
)