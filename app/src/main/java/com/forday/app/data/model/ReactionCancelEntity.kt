package com.forday.app.data.model

import com.forday.app.domain.model.ReactionCancelDomain

data class ReactionCancelEntity(
    val status: Int,
    val isSuccess: Boolean,
    val data: ReactionCancelDataEntity
) {
    fun toDomain(): ReactionCancelDomain {
        return ReactionCancelDomain(
            status = status,
            isSuccess = isSuccess,
            message = data.message,
            reactionType = data.reactionType,
            recordId = data.recordId,
            errorClassName = data.errorClassName
        )
    }
}

data class ReactionCancelDataEntity(
    val message: String,
    val reactionType: String,
    val recordId: Int,
    val errorClassName: String
)