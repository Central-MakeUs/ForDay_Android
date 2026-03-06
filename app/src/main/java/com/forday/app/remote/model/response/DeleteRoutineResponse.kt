package com.forday.app.remote.model.response

import com.forday.app.data.model.DeleteRoutineDataEntity
import com.forday.app.data.model.DeleteRoutineEntity
import com.forday.app.remote.RemoteMapper
import com.google.gson.annotations.SerializedName

data class DeleteRoutineResponse(
    @SerializedName("status") val status: Int,
    @SerializedName("success") val isSuccess: Boolean,
    @SerializedName("data") val data: DeleteRoutineData
) : RemoteMapper<DeleteRoutineEntity> {
    override fun toData(): DeleteRoutineEntity = DeleteRoutineEntity(
        status = status,
        isSuccess = isSuccess,
        data = data.toData()
    )
}

data class DeleteRoutineData(
    @SerializedName("message") val message: String,
    @SerializedName("errorClassName") val errorClassName: String? = null // 실패 시에만 존재
) : RemoteMapper<DeleteRoutineDataEntity> {
    override fun toData(): DeleteRoutineDataEntity = DeleteRoutineDataEntity(
        message = message,
        errorClassName = errorClassName
    )
}