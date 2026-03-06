package com.forday.app.remote.model.response

import com.forday.app.data.model.SwitchAccountEntity
import com.forday.app.remote.RemoteMapper
import com.google.gson.annotations.SerializedName

data class SwitchAccountResponse(
    @SerializedName("status")
    val status: Int,
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("data")
    val data: SwitchAccessTokenData
): RemoteMapper<SwitchAccountEntity> {
    override fun toData(): SwitchAccountEntity {
        return SwitchAccountEntity(
            socialType = data.socialType,
            accessToken = data.accessToken,
            refreshToken = data.refreshToken,
            errorClassName = data.errorClassName,
            message = data.message
        )
    }

}


data class SwitchAccessTokenData(
    @SerializedName("socialType")
    val socialType: String,
    @SerializedName("accessToken")
    val accessToken: String,
    @SerializedName("refreshToken")
    val refreshToken: String,

    // 에러 필드
    @SerializedName("errorClassName")
    val errorClassName: String?,
    @SerializedName("message")
    val message: String?
)

