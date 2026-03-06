package com.forday.app.remote.model.response

import com.forday.app.data.model.UpdateHobbyDurationDataEntity
import com.forday.app.data.model.UpdateHobbyDurationEntity
import com.forday.app.remote.RemoteMapper
import com.google.gson.annotations.SerializedName

data class UpdateHobbyDurationResponse(
    @SerializedName("status") val status: Int,
    @SerializedName("success") val isSuccess: Boolean,
    @SerializedName("data") val data: UpdateHobbyDurationData
) : RemoteMapper<UpdateHobbyDurationEntity> {
    override fun toData(): UpdateHobbyDurationEntity = UpdateHobbyDurationEntity(
        status = status,
        isSuccess = isSuccess,
        data = data.toData()
    )
}

data class UpdateHobbyDurationData(
    @SerializedName("message") val message: String,
    @SerializedName("errorClassName") val errorClassName: String? = null
) : RemoteMapper<UpdateHobbyDurationDataEntity> {
    override fun toData(): UpdateHobbyDurationDataEntity = UpdateHobbyDurationDataEntity(
        message = message,
        errorClassName = errorClassName
    )
}