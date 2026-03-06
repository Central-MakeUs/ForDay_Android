package com.forday.app.remote.model.response

import com.forday.app.data.model.UpdateHobbyStatusDataEntity
import com.forday.app.data.model.UpdateHobbyStatusEntity
import com.forday.app.remote.RemoteMapper
import com.google.gson.annotations.SerializedName

data class UpdateHobbyStatusResponse(
    @SerializedName("status") val status: Int,
    @SerializedName("success") val isSuccess: Boolean,
    @SerializedName("data") val data: UpdateHobbyStatusData
) : RemoteMapper<UpdateHobbyStatusEntity> {
    override fun toData(): UpdateHobbyStatusEntity = UpdateHobbyStatusEntity(
        status = status,
        isSuccess = isSuccess,
        data = data.toData()
    )
}

data class UpdateHobbyStatusData(
    @SerializedName("message") val message: String,
    @SerializedName("errorClassName") val errorClassName: String? = null // 예외 응답 대응
) : RemoteMapper<UpdateHobbyStatusDataEntity> {
    override fun toData(): UpdateHobbyStatusDataEntity = UpdateHobbyStatusDataEntity(
        message = message,
        errorClassName = errorClassName
    )
}