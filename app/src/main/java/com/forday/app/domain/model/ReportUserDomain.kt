package com.forday.app.domain.model

data class ReportUserDomain(
    val status: Int,
    val success: Boolean,
    val data: ReportUserDataDomain
)

data class ReportUserDataDomain(
    val message: String,
    val nickname: String,
    val userId: String
)
