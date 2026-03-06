package com.forday.app.domain.model

data class ReportPostingDomain(
    val status: Int,
    val success: Boolean,
    val data: ReportPostingDataDomain
)

data class ReportPostingDataDomain(
    val recordId: Int,
    val recordWriterId: String,
    val recordWriterNickname: String,
    val message: String
)
