package com.forday.app.remote.model.response

import com.forday.app.data.model.ReactionUserEntity
import com.forday.app.data.model.ReactionUsersDataEntity
import com.forday.app.data.model.ReactionUsersEntity
import com.forday.app.remote.RemoteMapper
import com.google.gson.annotations.SerializedName

data class ReactionUsersResponse(
    @SerializedName("status")
    val status: Int,
    @SerializedName("success")
    val isSuccess: Boolean,
    @SerializedName("data")
    val data: ReactionUsersDataResponse
) : RemoteMapper<ReactionUsersEntity> {
    override fun toData(): ReactionUsersEntity {
        return ReactionUsersEntity(
            status = status,
            isSuccess = isSuccess,
            data = data.toData()
        )
    }
}

data class ReactionUsersDataResponse(
    @SerializedName("reactionType")
    val reactionType: String?,
    @SerializedName("reactionUsers")
    val reactionUsers: List<ReactionUserResponse>?,
    @SerializedName("hasNext")
    val hasNext: Boolean?,
    @SerializedName("lastUserId")
    val lastUserId: String?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("errorClassName")
    val errorClassName: String?
) : RemoteMapper<ReactionUsersDataEntity> {
    override fun toData(): ReactionUsersDataEntity {
        return ReactionUsersDataEntity(
            reactionType = reactionType ?: "",
            reactionUsers = reactionUsers?.map { it.toData() } ?: emptyList(),
            hasNext = hasNext ?: false,
            lastUserId = lastUserId ?: "",
            message = message ?: "",
            errorClassName = errorClassName ?: ""
        )
    }
}

data class ReactionUserResponse(
    @SerializedName("userId")
    val userId: String,
    @SerializedName("nickname")
    val nickname: String,
    @SerializedName("profileImageUrl")
    val profileImageUrl: String?,
    @SerializedName("reactedAt")
    val reactedAt: String,
    @SerializedName("newReactionUser")
    val newReactionUser: Boolean?
) : RemoteMapper<ReactionUserEntity> {
    override fun toData(): ReactionUserEntity {
        return ReactionUserEntity(
            userId = userId,
            nickname = nickname,
            profileImageUrl = profileImageUrl,
            reactedAt = reactedAt,
            newReactionUser = newReactionUser ?: false
        )
    }
}