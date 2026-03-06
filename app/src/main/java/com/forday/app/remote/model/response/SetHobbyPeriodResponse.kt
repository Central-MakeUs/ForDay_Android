package com.forday.app.remote.model.response

import com.forday.app.data.model.SetHobbyPeriodDataEntity
import com.forday.app.data.model.SetHobbyPeriodEntity
import com.forday.app.remote.RemoteMapper
import com.google.gson.annotations.SerializedName

data class SetHobbyPeriodResponse(
    @SerializedName("status") val status: Int,
    @SerializedName("success") val isSuccess: Boolean,
    @SerializedName("data") val data: SetHobbyPeriodDataResponse
) : RemoteMapper<SetHobbyPeriodEntity> {
    override fun toData(): SetHobbyPeriodEntity = SetHobbyPeriodEntity(
        status = status,
        isSuccess = isSuccess,
        data = data.toData()
    )
}

data class SetHobbyPeriodDataResponse(
    @SerializedName("hobbyId") val hobbyId: Int? = null,
    @SerializedName("type") val type: String? = null,
    @SerializedName("message") val message: String? = null,
    @SerializedName("errorClassName") val errorClassName: String? = null
) : RemoteMapper<SetHobbyPeriodDataEntity> {
    override fun toData(): SetHobbyPeriodDataEntity = SetHobbyPeriodDataEntity(
        hobbyId = hobbyId ?: 0,
        type = type ?: "",
        message = message ?: "",
        errorClassName = errorClassName
    )
}