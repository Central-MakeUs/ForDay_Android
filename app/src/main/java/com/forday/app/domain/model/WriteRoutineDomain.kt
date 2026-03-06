package com.forday.app.domain.model

data class WriteRoutineDomain(
    val status: Int,
    val isSuccess: Boolean,
    val data: WriteRoutineDataDomain
)

data class WriteRoutineDataDomain(
    val message: String,
    val hobbyId: Int,
    val routineRecordId: Int,
    val routineContent: String,
    val imageUrl: String,
    val sticker: String,
    val memo: String,
    val extensionCheckRequired: Boolean,
    val errorClassName: String?
)