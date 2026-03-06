package com.forday.app.domain.model

data class DeleteRoutineDomain(
    val status: Int,
    val isSuccess: Boolean,
    val data: DeleteRoutineDataDomain
)

data class DeleteRoutineDataDomain(
    val message: String,
    val errorClassName: String?
)