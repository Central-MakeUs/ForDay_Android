package com.forday.app.data.model

import com.forday.app.data.DataMapper
import com.forday.app.domain.model.DeleteRoutineDataDomain
import com.forday.app.domain.model.DeleteRoutineDomain

data class DeleteRoutineEntity(
    val status: Int,
    val isSuccess: Boolean,
    val data: DeleteRoutineDataEntity
) : DataMapper<DeleteRoutineDomain> {
    override fun toDomain(): DeleteRoutineDomain = DeleteRoutineDomain(
        status = status,
        isSuccess = isSuccess,
        data = data.toDomain()
    )
}

data class DeleteRoutineDataEntity(
    val message: String,
    val errorClassName: String?
) : DataMapper<DeleteRoutineDataDomain> {
    override fun toDomain(): DeleteRoutineDataDomain = DeleteRoutineDataDomain(
        message = message,
        errorClassName = errorClassName
    )
}