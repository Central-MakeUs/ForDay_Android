package com.forday.app.domain.model

data class CancelScrapDomain(
    val message: String,
    val recordId: Int,
    val isScraped: Boolean
)