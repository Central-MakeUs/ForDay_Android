package com.forday.app.data.model

import com.forday.app.data.DataMapper
import com.forday.app.domain.model.ScrapDataDomain
import com.forday.app.domain.model.ScrapDomain

data class ScrapEntity(
    val status: Int,
    val isSuccess: Boolean,
    val data: ScrapDataEntity
): DataMapper<ScrapDomain> {
    override fun toDomain(): ScrapDomain {
        return ScrapDomain(
            status = status,
            isSuccess = isSuccess,
            data = data.toDomain()
        )
    }
}

data class ScrapDataEntity(
    val message: String,
    val recordId: Int,
    val scraped: Boolean
): DataMapper<ScrapDataDomain> {
    override fun toDomain(): ScrapDataDomain {
        return ScrapDataDomain(
            message = message,
            recordId = recordId,
            scraped = scraped
        )
    }
}