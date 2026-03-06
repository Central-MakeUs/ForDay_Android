package com.forday.app.data.model

import com.forday.app.data.DataMapper
import com.forday.app.domain.model.UpdateHobbyStatusDataDomain
import com.forday.app.domain.model.UpdateHobbyStatusDomain

data class UpdateHobbyStatusEntity(
    val status: Int,
    val isSuccess: Boolean,
    val data: UpdateHobbyStatusDataEntity
) : DataMapper<UpdateHobbyStatusDomain> {
    override fun toDomain(): UpdateHobbyStatusDomain = UpdateHobbyStatusDomain(
        status = status,
        isSuccess = isSuccess,
        data = data.toDomain()
    )
}

data class UpdateHobbyStatusDataEntity(
    val message: String,
    val errorClassName: String?
) : DataMapper<UpdateHobbyStatusDataDomain> {
    override fun toDomain(): UpdateHobbyStatusDataDomain = UpdateHobbyStatusDataDomain(
        message = message,
        errorClassName = errorClassName
    )
}