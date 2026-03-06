package com.forday.app.remote.model.response

import com.forday.app.data.model.ReportUserDataEntity
import com.forday.app.data.model.ReportUserEntity
import com.forday.app.remote.RemoteMapper
import com.google.gson.annotations.SerializedName

data class ReportUserResponse(
    @SerializedName("status") val status: Int?,
    @SerializedName("success") val success: Boolean?,
    @SerializedName("data") val data: ReportUserDataResponse?
) : RemoteMapper<ReportUserEntity> {
    override fun toData(): ReportUserEntity = ReportUserEntity(
        status = status ?: 0,
        success = success ?: false,
        data = data?.toData() ?: ReportUserDataEntity()
    )
}

data class ReportUserDataResponse(
    @SerializedName("message") val message: String?,
    @SerializedName("nickname") val nickname: String?,
    @SerializedName("userId") val userId: String?
) : RemoteMapper<ReportUserDataEntity> {
    override fun toData(): ReportUserDataEntity = ReportUserDataEntity(
        message = message ?: "",
        nickname = nickname ?: "",
        userId = userId ?: ""
    )
}
