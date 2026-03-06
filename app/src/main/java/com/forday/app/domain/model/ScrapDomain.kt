package com.forday.app.domain.model

data class ScrapDomain(
    val status: Int,
    val isSuccess: Boolean,
    val data: ScrapDataDomain
)

data class ScrapDataDomain(
    val message: String,
    val recordId: Int,
    val scraped: Boolean
)