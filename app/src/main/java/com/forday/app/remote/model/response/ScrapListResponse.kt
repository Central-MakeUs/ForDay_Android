package com.forday.app.remote.model.response

import com.forday.app.data.model.*
import com.forday.app.remote.RemoteMapper
import com.google.gson.annotations.SerializedName
import kotlin.collections.map

data class ScrapListResponse(
    @SerializedName("status") val status: Int,
    @SerializedName("success") val isSuccess: Boolean,
    @SerializedName("data") val data: ScrapListDataResponse
) : RemoteMapper<ScrapListEntity> {
    override fun toData(): ScrapListEntity = ScrapListEntity(
        status = status,
        isSuccess = isSuccess,
        data = data.toData()
    )
}

data class ScrapListDataResponse(
    @SerializedName("totalScrapCount") val totalScrapCount: Int,
    @SerializedName("lastScrapId") val lastScrapId: Int,
    @SerializedName("scrapList") val scrapList: List<ScrapItemResponse>,
    @SerializedName("hasNext") val hasNext: Boolean
) : RemoteMapper<ScrapListDataEntity> {
    override fun toData(): ScrapListDataEntity = ScrapListDataEntity(
        totalScrapCount = totalScrapCount,
        lastScrapId = lastScrapId,
        scrapList = scrapList.map { it.toData() },
        hasNext = hasNext
    )
}

data class ScrapItemResponse(
    @SerializedName("scrapId") val scrapId: Int,
    @SerializedName("recordId") val recordId: Int,
    @SerializedName("thumbnailImageUrl") val thumbnailImageUrl: String,
    @SerializedName("sticker") val sticker: String,
    @SerializedName("memo") val memo: String,
    @SerializedName("createdAt") val createdAt: String
) : RemoteMapper<ScrapItemEntity> {
    override fun toData(): ScrapItemEntity = ScrapItemEntity(
        scrapId = scrapId,
        recordId = recordId,
        thumbnailImageUrl = thumbnailImageUrl,
        sticker = sticker,
        memo = memo,
        createdAt = createdAt
    )
}