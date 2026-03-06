package com.forday.app.remote.model.response

import com.forday.app.data.model.CancelAccountDataEntity
import com.forday.app.data.model.CancelAccountEntity
import com.forday.app.remote.RemoteMapper
import com.google.gson.annotations.SerializedName

data class CancelAccountResponse(
    @SerializedName("status")
    val status: Int,
    @SerializedName("success")
    val isSuccess: Boolean,
    @SerializedName("data")
    val data: CancelAccountDataResponse
) : RemoteMapper<CancelAccountEntity> {
    override fun toData(): CancelAccountEntity =
        CancelAccountEntity(
            status = status,
            isSuccess = isSuccess,
            data = data.toData()
        )
}

data class CancelAccountDataResponse(
    @SerializedName("message")
    val message: String,
    @SerializedName("deletedAt")
    val deletedAt: String
) : RemoteMapper<CancelAccountDataEntity> {
    override fun toData(): CancelAccountDataEntity =
        CancelAccountDataEntity(
            message = message,
            deletedAt = deletedAt
        )
}