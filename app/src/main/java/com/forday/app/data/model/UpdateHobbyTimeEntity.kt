package com.forday.app.data.model

import com.forday.app.data.DataMapper
import com.forday.app.domain.model.UpdateHobbyTimeDataDomain
import com.forday.app.domain.model.UpdateHobbyTimeDomain

data class UpdateHobbyTimeEntity(
    val status: Int,
    val isSuccess: Boolean,
    val data: UpdateHobbyTimeDataEntity
) : DataMapper<UpdateHobbyTimeDomain> {
    override fun toDomain(): UpdateHobbyTimeDomain = UpdateHobbyTimeDomain(
        status = status,
        isSuccess = isSuccess,
        data = data.toDomain()
    )
}

data class UpdateHobbyTimeDataEntity(
    val message: String,
    val errorClassName: String?
) : DataMapper<UpdateHobbyTimeDataDomain> {
    override fun toDomain(): UpdateHobbyTimeDataDomain = UpdateHobbyTimeDataDomain(
        message = message,
        errorClassName = errorClassName
    )
}