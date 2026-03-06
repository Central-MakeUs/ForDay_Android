package com.forday.app.remote.model.response

import com.forday.app.data.model.VisibilityDataEntity
import com.forday.app.data.model.VisibilityEntity
import com.forday.app.remote.RemoteMapper
import com.google.gson.annotations.SerializedName

data class VisibilityResponse(
    @SerializedName("status")
    val status: Int,
    @SerializedName("success")
    val isSuccess: Boolean,
    @SerializedName("data")
    val data: VisibilityDataResponse
) : RemoteMapper<VisibilityEntity> {
    override fun toData(): VisibilityEntity {
        return VisibilityEntity(
            status = status,
            isSuccess = isSuccess,
            data = data.toData()
        )
    }
}

data class VisibilityDataResponse(
    @SerializedName("message")
    val message: String,
    // 성공 시 필드
    @SerializedName("previousVisibility")
    val previousVisibility: String?,
    @SerializedName("newVisibility")
    val newVisibility: String?,
    // 실패 시 필드
    @SerializedName("errorClassName")
    val errorClassName: String?
) : RemoteMapper<VisibilityDataEntity> {
    override fun toData(): VisibilityDataEntity {
        return VisibilityDataEntity(
            message = message,
            previousVisibility = previousVisibility ?: "",
            newVisibility = newVisibility ?: "",
            errorClassName = errorClassName ?: ""
        )
    }
}