package com.forday.app.data.model

import com.forday.app.domain.model.CancelScrapDomain

data class CancelScrapEntity(
    val status: Int,
    val success: Boolean,
    val data: CancelScrapDataEntity
) {
    fun toDomain(): CancelScrapDomain {
        return CancelScrapDomain(
            message = data.message,
            recordId = data.recordId,
            isScraped = data.scraped
        )
    }
}

data class CancelScrapDataEntity(
    val message: String,
    val recordId: Int,
    val scraped: Boolean
)