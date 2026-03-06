package com.forday.app.data.model

import com.forday.app.domain.model.DeletePostingDomain

data class DeletePostingEntity(
    val status: Int,
    val isSuccess: Boolean,
    val data: DeletePostingDataEntity
) {
    fun toDomain(): DeletePostingDomain {
        return DeletePostingDomain(
            isSuccess = isSuccess,
            message = data.message,
            recordId = data.recordId,
            errorClassName = data.errorClassName
        )
    }
}

data class DeletePostingDataEntity(
    val message: String,
    val recordId: Int,
    val errorClassName: String
)