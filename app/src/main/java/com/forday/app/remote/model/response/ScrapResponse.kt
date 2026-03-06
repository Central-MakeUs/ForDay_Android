package com.forday.app.remote.model.response

import com.forday.app.data.model.ScrapDataEntity
import com.forday.app.data.model.ScrapEntity
import com.forday.app.remote.RemoteMapper
import com.google.gson.annotations.SerializedName

data class ScrapResponse(
    @SerializedName("status")
    val status: Int,
    @SerializedName("success")
    val isSuccess: Boolean,
    @SerializedName("data")
    val data: ScrapData
): RemoteMapper<ScrapEntity> {
    override fun toData(): ScrapEntity {
        return ScrapEntity(
            status = status,
            isSuccess = isSuccess,
            data = data.toData()
        )
    }
}

data class ScrapData(
    @SerializedName("message")
    val message: String,
    @SerializedName("recordId")
    val recordId: Int,
    @SerializedName("scraped")
    val scraped: Boolean
): RemoteMapper<ScrapDataEntity> {
    override fun toData(): ScrapDataEntity {
        return ScrapDataEntity(
            message = message,
            recordId = recordId,
            scraped = scraped
        )
    }
}