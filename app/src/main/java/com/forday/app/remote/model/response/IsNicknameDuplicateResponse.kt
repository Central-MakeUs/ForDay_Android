package com.forday.app.remote.model.response

import com.forday.app.data.model.IsNicknameDuplicateDataEntity
import com.forday.app.data.model.IsNicknameDuplicateEntity
import com.forday.app.remote.RemoteMapper
import com.google.gson.annotations.SerializedName

data class IsNicknameDuplicateResponse(
    @SerializedName("status")
    val status: Int,

    @SerializedName("success")
    val isSuccess: Boolean,

    @SerializedName("data")
    val data: NicknameDuplicateData
) : RemoteMapper<IsNicknameDuplicateEntity> {
    override fun toData(): IsNicknameDuplicateEntity {
        return IsNicknameDuplicateEntity(
            status = status,
            isSuccess = isSuccess,
            data = data.toData()
        )
    }
}

data class NicknameDuplicateData(
    @SerializedName("nickname")
    val nickname: String,

    @SerializedName("message")
    val message: String,

    @SerializedName("available")
    val available: Boolean
) : RemoteMapper<IsNicknameDuplicateDataEntity> {
    override fun toData(): IsNicknameDuplicateDataEntity {
        return IsNicknameDuplicateDataEntity(
            nickname = nickname,
            message = message,
            available = available
        )
    }
}