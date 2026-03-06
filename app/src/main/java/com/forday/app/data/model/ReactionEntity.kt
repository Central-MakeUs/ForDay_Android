package com.forday.app.data.model

import com.forday.app.domain.model.ReactionDomain

data class ReactionEntity(
    val status: Int,
    val isSuccess: Boolean,
    val data: ReactionDataEntity
) {
    fun toDomain(): ReactionDomain {
        return ReactionDomain(
            status = status,
            isSuccess = isSuccess,
            message = data.message,
            reactionType = data.reactionType,
            recordId = data.recordId,
            errorClassName = data.errorClassName
        )
    }
}

data class ReactionDataEntity(
    val message: String,
    val reactionType: String,
    val recordId: Int,
    val errorClassName: String
)