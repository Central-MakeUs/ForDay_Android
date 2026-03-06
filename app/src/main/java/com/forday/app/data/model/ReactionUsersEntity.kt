package com.forday.app.data.model

import com.forday.app.domain.model.ReactionDetailDomain
import com.forday.app.domain.model.ReactionUserInfo

data class ReactionUsersEntity(
    val status: Int,
    val isSuccess: Boolean,
    val data: ReactionUsersDataEntity
) {
    fun toDomain(): ReactionDetailDomain {
        return ReactionDetailDomain(
            status = status,
            isSuccess = isSuccess,
            reactionType = data.reactionType,
            users = data.reactionUsers.map { it.toDomain() },
            hasNext = data.hasNext,
            lastUserId = data.lastUserId,
            message = data.message,
            errorClassName = data.errorClassName
        )
    }
}

data class ReactionUsersDataEntity(
    val reactionType: String,
    val reactionUsers: List<ReactionUserEntity>,
    val hasNext: Boolean,
    val lastUserId: String,
    val message: String,
    val errorClassName: String
)

data class ReactionUserEntity(
    val userId: String,
    val nickname: String,
    val profileImageUrl: String?,
    val reactedAt: String,
    val newReactionUser: Boolean
) {
    fun toDomain(): ReactionUserInfo {
        return ReactionUserInfo(
            userId = userId,
            nickname = nickname,
            profileImageUrl = profileImageUrl,
            reactedAt = reactedAt,
            newReactionUser = newReactionUser
        )
    }
}