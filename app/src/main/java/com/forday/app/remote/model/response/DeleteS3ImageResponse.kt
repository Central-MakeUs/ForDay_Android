package com.forday.app.remote.model.response

import com.forday.app.data.model.DeleteS3ImageDataEntity
import com.forday.app.data.model.DeleteS3ImageEntity
import com.forday.app.remote.RemoteMapper
import com.google.gson.annotations.SerializedName

data class DeleteS3ImageResponse(
    @SerializedName("status") val status: Int,
    @SerializedName("success") val isSuccess: Boolean,
    @SerializedName("data") val data: DeleteS3ImageDataResponse
) : RemoteMapper<DeleteS3ImageEntity> {
    override fun toData(): DeleteS3ImageEntity = DeleteS3ImageEntity(
        status = status,
        isSuccess = isSuccess,
        data = data.toData()
    )
}

data class DeleteS3ImageDataResponse(
    @SerializedName("message") val message: String? = null,
    @SerializedName("errorClassName") val errorClassName: String? = null
) : RemoteMapper<DeleteS3ImageDataEntity> {
    override fun toData(): DeleteS3ImageDataEntity = DeleteS3ImageDataEntity(
        message = message ?: "",
        errorClassName = errorClassName
    )
}