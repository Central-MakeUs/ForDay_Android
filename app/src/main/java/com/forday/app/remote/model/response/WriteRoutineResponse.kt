package com.forday.app.remote.model.response

import com.forday.app.data.model.WriteRoutineDataEntity
import com.forday.app.data.model.WriteRoutineEntity
import com.forday.app.remote.RemoteMapper
import com.google.gson.annotations.SerializedName

data class WriteRoutineResponse(
    @SerializedName("status") val status: Int,
    @SerializedName("success") val isSuccess: Boolean,
    @SerializedName("data") val data: WriteRoutineDataResponse
) : RemoteMapper<WriteRoutineEntity> {
    override fun toData(): WriteRoutineEntity = WriteRoutineEntity(
        status = status,
        isSuccess = isSuccess,
        data = data.toData()
    )
}

data class WriteRoutineDataResponse(
    @SerializedName("message") val message: String? = null,
    @SerializedName("hobbyId") val hobbyId: Int? = null,
    @SerializedName("activityRecordId") val routineRecordId: Int? = null,
    @SerializedName("activityContent") val routineContent: String? = null,
    @SerializedName("imageUrl") val imageUrl: String? = null,
    @SerializedName("sticker") val sticker: String? = null,
    @SerializedName("memo") val memo: String? = null,
    @SerializedName("extensionCheckRequired") val extensionCheckRequired: Boolean? = null,
    @SerializedName("errorClassName") val errorClassName: String? = null
) : RemoteMapper<WriteRoutineDataEntity> {
    override fun toData(): WriteRoutineDataEntity = WriteRoutineDataEntity(
        message = message ?: "",
        hobbyId = hobbyId ?: 0,
        routineRecordId = routineRecordId ?: 0,
        routineContent = routineContent ?: "",
        imageUrl = imageUrl ?: "",
        sticker = sticker ?: "",
        memo = memo ?: "",
        extensionCheckRequired = extensionCheckRequired ?: false,
        errorClassName = errorClassName
    )
}