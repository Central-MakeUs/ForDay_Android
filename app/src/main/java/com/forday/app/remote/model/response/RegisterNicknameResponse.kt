package com.forday.app.remote.model.response

import com.forday.app.data.model.RegisterNicknameDataEntity
import com.forday.app.data.model.RegisterNicknameEntity
import com.forday.app.remote.RemoteMapper
import com.google.gson.annotations.SerializedName

data class RegisterNicknameResponse(
    @SerializedName("status")
    val status: Int,
    @SerializedName("success")
    val isSuccess: Boolean,
    @SerializedName("data")
    val data: RegisterNicknameData
) : RemoteMapper<RegisterNicknameEntity> {
    override fun toData(): RegisterNicknameEntity =
        RegisterNicknameEntity(
            status = status,
            isSuccess = isSuccess,
            data = data.toData()
        )
}

data class RegisterNicknameData(
    @SerializedName("message")
    val message: String,
    @SerializedName("nickname")
    val nickname: String,
) : RemoteMapper<RegisterNicknameDataEntity> {
    override fun toData(): RegisterNicknameDataEntity =
        RegisterNicknameDataEntity(
            message = message,
            nickname = nickname
        )
}