package com.forday.app.data.model

import com.forday.app.data.DataMapper
import com.forday.app.domain.model.ReportUserDataDomain
import com.forday.app.domain.model.ReportUserDomain

data class ReportUserEntity(
    val status: Int,
    val success: Boolean,
    val data: ReportUserDataEntity
) : DataMapper<ReportUserDomain> {
    override fun toDomain(): ReportUserDomain = ReportUserDomain(
        status = status,
        success = success,
        data = data.toDomain()
    )
}

data class ReportUserDataEntity(
    val message: String = "",
    val nickname: String = "",
    val userId: String = ""
) : DataMapper<ReportUserDataDomain> {
    override fun toDomain(): ReportUserDataDomain = ReportUserDataDomain(
        message = message,
        nickname = nickname,
        userId = userId
    )
}
