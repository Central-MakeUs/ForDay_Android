package com.forday.app.data.model

import com.forday.app.data.DataMapper
import com.forday.app.domain.model.UpdateRoutineDataDomain
import com.forday.app.domain.model.UpdateRoutineDomain

data class UpdateRoutineEntity(
    val status: Int,
    val isSuccess: Boolean,
    val data: UpdateRoutineDataEntity
) : DataMapper<UpdateRoutineDomain> {
    override fun toDomain(): UpdateRoutineDomain = UpdateRoutineDomain(
        status = status,
        isSuccess = isSuccess,
        data = data.toDomain()
    )
}

data class UpdateRoutineDataEntity(
    val message: String,
    val errorClassName: String?
) : DataMapper<UpdateRoutineDataDomain> {
    override fun toDomain(): UpdateRoutineDataDomain = UpdateRoutineDataDomain(
        message = message,
        errorClassName = errorClassName
    )
}