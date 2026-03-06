package com.forday.app.remote.model.response

import com.forday.app.data.model.UpdateRoutineDataEntity
import com.forday.app.data.model.UpdateRoutineEntity
import com.forday.app.remote.RemoteMapper
import com.google.gson.annotations.SerializedName

data class UpdateRoutineResponse(
    @SerializedName("status") val status: Int,
    @SerializedName("success") val isSuccess: Boolean,
    @SerializedName("data") val data: UpdateRoutineData
) : RemoteMapper<UpdateRoutineEntity> {
    override fun toData(): UpdateRoutineEntity = UpdateRoutineEntity(
        status = status,
        isSuccess = isSuccess,
        data = data.toData()
    )
}

data class UpdateRoutineData(
    @SerializedName("message") val message: String,
    @SerializedName("errorClassName") val errorClassName: String? = null // 예외 시에만 존재
) : RemoteMapper<UpdateRoutineDataEntity> {
    override fun toData(): UpdateRoutineDataEntity = UpdateRoutineDataEntity(
        message = message,
        errorClassName = errorClassName
    )
}