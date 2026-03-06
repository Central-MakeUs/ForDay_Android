package com.forday.app.domain.model

data class CreateRoutinesDomain(
    val status: Int,
    val isSuccess: Boolean,
    val data: CreateRoutinesDataDomain
)

data class CreateRoutinesDataDomain(
    val message: String,
    val createdRoutineNum: Int
)