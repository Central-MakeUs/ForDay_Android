package com.forday.app.remote.model.response

import com.forday.app.data.model.UpdateHobbyTimeDataEntity
import com.forday.app.data.model.UpdateHobbyTimeEntity
import com.forday.app.remote.RemoteMapper
import com.google.gson.annotations.SerializedName

data class UpdateHobbyTimeResponse(
    @SerializedName("status") val status: Int,
    @SerializedName("success") val isSuccess: Boolean,
    @SerializedName("data") val data: UpdateHobbyTimeData
) : RemoteMapper<UpdateHobbyTimeEntity> {
    override fun toData(): UpdateHobbyTimeEntity = UpdateHobbyTimeEntity(
        status = status,
        isSuccess = isSuccess,
        data = data.toData()
    )
}

data class UpdateHobbyTimeData(
    @SerializedName("message") val message: String,
    @SerializedName("errorClassName") val errorClassName: String? = null // 예외 응답 대응
) : RemoteMapper<UpdateHobbyTimeDataEntity> {
    override fun toData(): UpdateHobbyTimeDataEntity = UpdateHobbyTimeDataEntity(
        message = message,
        errorClassName = errorClassName
    )
}