package com.forday.app.remote.model.response

import com.forday.app.data.model.CancelScrapDataEntity
import com.forday.app.data.model.CancelScrapEntity
import com.forday.app.remote.RemoteMapper
import com.google.gson.annotations.SerializedName

data class CancelScrapResponse(
    @SerializedName("status") val status: Int,
    @SerializedName("success") val success: Boolean,
    @SerializedName("data") val data: CancelScrapDataResponse
) : RemoteMapper<CancelScrapEntity> {
    override fun toData(): CancelScrapEntity {
        return CancelScrapEntity(
            status = status,
            success = success,
            data = data.toData()
        )
    }
}

data class CancelScrapDataResponse(
    @SerializedName("message") val message: String,
    @SerializedName("recordId") val recordId: Int,
    @SerializedName("scraped") val scraped: Boolean
) : RemoteMapper<CancelScrapDataEntity> {
    override fun toData(): CancelScrapDataEntity {
        return CancelScrapDataEntity(
            message = message,
            recordId = recordId,
            scraped = scraped
        )
    }
}