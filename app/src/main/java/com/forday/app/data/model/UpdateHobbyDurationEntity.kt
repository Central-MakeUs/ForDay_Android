package com.forday.app.data.model

import com.forday.app.data.DataMapper
import com.forday.app.domain.model.UpdateHobbyDurationDataDomain
import com.forday.app.domain.model.UpdateHobbyDurationDomain

data class UpdateHobbyDurationEntity(
    val status: Int,
    val isSuccess: Boolean,
    val data: UpdateHobbyDurationDataEntity
) : DataMapper<UpdateHobbyDurationDomain> {
    override fun toDomain(): UpdateHobbyDurationDomain = UpdateHobbyDurationDomain(
        status = status,
        isSuccess = isSuccess,
        data = data.toDomain()
    )
}

data class UpdateHobbyDurationDataEntity(
    val message: String,
    val errorClassName: String?
) : DataMapper<UpdateHobbyDurationDataDomain> {
    override fun toDomain(): UpdateHobbyDurationDataDomain = UpdateHobbyDurationDataDomain(
        message = message,
        errorClassName = errorClassName
    )
}