package com.forday.app.data.model

import com.forday.app.data.DataMapper
import com.forday.app.domain.model.WriteRoutineDataDomain
import com.forday.app.domain.model.WriteRoutineDomain

data class WriteRoutineEntity(
    val status: Int,
    val isSuccess: Boolean,
    val data: WriteRoutineDataEntity
) : DataMapper<WriteRoutineDomain> {
    override fun toDomain(): WriteRoutineDomain = WriteRoutineDomain(
        status = status,
        isSuccess = isSuccess,
        data = data.toDomain()
    )
}

data class WriteRoutineDataEntity(
    val message: String,
    val hobbyId: Int,
    val routineRecordId: Int,
    val routineContent: String,
    val imageUrl: String,
    val sticker: String,
    val memo: String,
    val extensionCheckRequired: Boolean,
    val errorClassName: String?
) : DataMapper<WriteRoutineDataDomain> {
    override fun toDomain(): WriteRoutineDataDomain = WriteRoutineDataDomain(
        message = message,
        hobbyId = hobbyId,
        routineRecordId = routineRecordId,
        routineContent = routineContent,
        imageUrl = imageUrl,
        sticker = sticker,
        memo = memo,
        extensionCheckRequired = extensionCheckRequired,
        errorClassName = errorClassName
    )
}