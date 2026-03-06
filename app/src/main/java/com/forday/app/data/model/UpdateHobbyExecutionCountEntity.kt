package com.forday.app.data.model

import com.forday.app.data.DataMapper
import com.forday.app.domain.model.UpdateHobbyExecutionCountDataDomain
import com.forday.app.domain.model.UpdateHobbyExecutionCountDomain

data class UpdateHobbyExecutionCountEntity(
    val status: Int,
    val isSuccess: Boolean,
    val data: UpdateHobbyExecutionCountDataEntity
) : DataMapper<UpdateHobbyExecutionCountDomain> {
    override fun toDomain(): UpdateHobbyExecutionCountDomain = UpdateHobbyExecutionCountDomain(
        status = status,
        isSuccess = isSuccess,
        data = data.toDomain()
    )
}

data class UpdateHobbyExecutionCountDataEntity(
    val message: String,
    val errorClassName: String?
) : DataMapper<UpdateHobbyExecutionCountDataDomain> {
    override fun toDomain(): UpdateHobbyExecutionCountDataDomain = UpdateHobbyExecutionCountDataDomain(
        message = message,
        errorClassName = errorClassName
    )
}