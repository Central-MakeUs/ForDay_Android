package com.forday.app.remote.model.response

import com.forday.app.data.model.UpdateHobbyExecutionCountDataEntity
import com.forday.app.data.model.UpdateHobbyExecutionCountEntity
import com.forday.app.remote.RemoteMapper
import com.google.gson.annotations.SerializedName

data class UpdateHobbyExecutionCountResponse(
    @SerializedName("status") val status: Int,
    @SerializedName("success") val isSuccess: Boolean,
    @SerializedName("data") val data: UpdateHobbyExecutionCountData
) : RemoteMapper<UpdateHobbyExecutionCountEntity> {
    override fun toData(): UpdateHobbyExecutionCountEntity = UpdateHobbyExecutionCountEntity(
        status = status,
        isSuccess = isSuccess,
        data = data.toData()
    )
}

data class UpdateHobbyExecutionCountData(
    @SerializedName("message") val message: String,
    @SerializedName("errorClassName") val errorClassName: String? = null
) : RemoteMapper<UpdateHobbyExecutionCountDataEntity> {
    override fun toData(): UpdateHobbyExecutionCountDataEntity = UpdateHobbyExecutionCountDataEntity(
        message = message,
        errorClassName = errorClassName
    )
}