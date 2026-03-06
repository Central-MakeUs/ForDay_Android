package com.forday.app.data.model

import com.forday.app.data.DataMapper
import com.forday.app.domain.model.CreateRoutinesDataDomain
import com.forday.app.domain.model.CreateRoutinesDomain

data class CreateRoutinesEntity(
    val status: Int,
    val isSuccess: Boolean,
    val data: CreateRoutinesDataEntity
) : DataMapper<CreateRoutinesDomain> {
    override fun toDomain(): CreateRoutinesDomain {
        return CreateRoutinesDomain(
            status = status,
            isSuccess = isSuccess,
            data = data.toDomain()
        )
    }
}

data class CreateRoutinesDataEntity(
    val message: String,
    val createdRoutineNum: Int
) : DataMapper<CreateRoutinesDataDomain> {
    override fun toDomain(): CreateRoutinesDataDomain {
        return CreateRoutinesDataDomain(
            message = message,
            createdRoutineNum = createdRoutineNum,
        )
    }
}