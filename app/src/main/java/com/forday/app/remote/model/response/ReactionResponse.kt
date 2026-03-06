package com.forday.app.remote.model.response

import com.forday.app.data.model.ReactionDataEntity
import com.forday.app.data.model.ReactionEntity
import com.forday.app.remote.RemoteMapper
import com.google.gson.annotations.SerializedName

data class ReactionResponse(
    @SerializedName("status")
    val status: Int,
    @SerializedName("success")
    val isSuccess: Boolean,
    @SerializedName("data")
    val data: ReactionDataResponse
) : RemoteMapper<ReactionEntity> {
    override fun toData(): ReactionEntity {
        return ReactionEntity(
            status = status,
            isSuccess = isSuccess,
            data = data.toData()
        )
    }
}

data class ReactionDataResponse(
    @SerializedName("message")
    val message: String,
    // 성공 시 필드
    @SerializedName("reactionType")
    val reactionType: String?,
    @SerializedName("recordId")
    val recordId: Int?,
    // 실패 시 필드
    @SerializedName("errorClassName")
    val errorClassName: String?
) : RemoteMapper<ReactionDataEntity> {
    override fun toData(): ReactionDataEntity {
        return ReactionDataEntity(
            message = message,
            reactionType = reactionType ?: "",
            recordId = recordId ?: -1,
            errorClassName = errorClassName ?: ""
        )
    }
}