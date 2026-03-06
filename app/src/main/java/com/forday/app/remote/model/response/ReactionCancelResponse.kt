package com.forday.app.remote.model.response

import com.forday.app.data.model.ReactionCancelDataEntity
import com.forday.app.data.model.ReactionCancelEntity
import com.forday.app.remote.RemoteMapper
import com.google.gson.annotations.SerializedName

data class ReactionCancelResponse(
    @SerializedName("status")
    val status: Int,
    @SerializedName("success")
    val isSuccess: Boolean,
    @SerializedName("data")
    val data: ReactionCancelDataResponse
) : RemoteMapper<ReactionCancelEntity> {
    override fun toData(): ReactionCancelEntity {
        return ReactionCancelEntity(
            status = status,
            isSuccess = isSuccess,
            data = data.toData()
        )
    }
}

data class ReactionCancelDataResponse(
    @SerializedName("message")
    val message: String,
    @SerializedName("reactionType")
    val reactionType: String?,
    @SerializedName("recordId")
    val recordId: Int?,
    // 예외 응답 대응을 위한 선택적 필드
    @SerializedName("errorClassName")
    val errorClassName: String? = null
) : RemoteMapper<ReactionCancelDataEntity> {
    override fun toData(): ReactionCancelDataEntity {
        return ReactionCancelDataEntity(
            message = message,
            reactionType = reactionType ?: "",
            recordId = recordId ?: -1,
            errorClassName = errorClassName ?: ""
        )
    }
}