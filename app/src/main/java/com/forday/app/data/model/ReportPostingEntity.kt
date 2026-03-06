package com.forday.app.data.model

import com.forday.app.data.DataMapper
import com.forday.app.domain.model.ReportPostingDataDomain
import com.forday.app.domain.model.ReportPostingDomain

data class ReportPostingEntity(
    val status: Int,
    val success: Boolean,
    val data: ReportPostingDataEntity
) : DataMapper<ReportPostingDomain> {
    override fun toDomain(): ReportPostingDomain = ReportPostingDomain(
        status = status,
        success = success,
        data = data.toDomain()
    )
}

data class ReportPostingDataEntity(
    val recordId: Int = 0,
    val recordWriterId: String = "",
    val recordWriterNickname: String = "",
    val message: String = ""
) : DataMapper<ReportPostingDataDomain> {
    override fun toDomain(): ReportPostingDataDomain = ReportPostingDataDomain(
        recordId = recordId,
        recordWriterId = recordWriterId,
        recordWriterNickname = recordWriterNickname,
        message = message
    )
}
