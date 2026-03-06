package com.forday.app.domain.model

data class UpdateRoutineDomain(
    val status: Int,
    val isSuccess: Boolean,
    val data: UpdateRoutineDataDomain
)

data class UpdateRoutineDataDomain(
    val message: String,
    val errorClassName: String?
)