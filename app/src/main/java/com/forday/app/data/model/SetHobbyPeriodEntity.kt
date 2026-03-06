package com.forday.app.data.model

import com.forday.app.data.DataMapper
import com.forday.app.domain.model.SetHobbyPeriodDataDomain
import com.forday.app.domain.model.SetHobbyPeriodDomain

data class SetHobbyPeriodEntity(
    val status: Int,
    val isSuccess: Boolean,
    val data: SetHobbyPeriodDataEntity
) : DataMapper<SetHobbyPeriodDomain> {
    override fun toDomain(): SetHobbyPeriodDomain = SetHobbyPeriodDomain(
        status = status,
        isSuccess = isSuccess,
        data = data.toDomain()
    )
}

data class SetHobbyPeriodDataEntity(
    val hobbyId: Int,
    val type: String,
    val message: String,
    val errorClassName: String?
) : DataMapper<SetHobbyPeriodDataDomain> {
    override fun toDomain(): SetHobbyPeriodDataDomain = SetHobbyPeriodDataDomain(
        hobbyId = hobbyId,
        type = type,
        message = message,
        errorClassName = errorClassName
    )
}