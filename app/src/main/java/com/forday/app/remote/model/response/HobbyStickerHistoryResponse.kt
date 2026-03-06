package com.forday.app.remote.model.response

import com.forday.app.data.model.HobbyStickerHistoryEntity
import com.forday.app.data.model.StickerEntity
import com.forday.app.remote.RemoteMapper
import com.google.gson.annotations.SerializedName

/**
 * API 최상위 응답을 처리하기 위한 Wrapper 클래스
 */
data class HobbyStickerHistoryWrapperResponse(
    @SerializedName("status") val status: Int,
    @SerializedName("success") val success: Boolean,
    @SerializedName("data") val data: HobbyStickerHistoryResponse? = null,
)

/**
 * 기존의 HobbyStickerHistoryResponse (JSON의 "data" 부분)
 */
data class HobbyStickerHistoryResponse(
    @SerializedName("hobbyId") val hobbyId: Int,
    @SerializedName("durationSet") val durationSet: Boolean,
    @SerializedName("activityRecordedToday") val activityRecordedToday: Boolean,
    @SerializedName("currentPage") val currentPage: Int,
    @SerializedName("totalPage") val totalPage: Int,
    @SerializedName("pageSize") val pageSize: Int,
    @SerializedName("totalStickerNum") val totalStickerNum: Int,
    @SerializedName("hasPrevious") val hasPrevious: Boolean,
    @SerializedName("hasNext") val hasNext: Boolean,
    @SerializedName("stickers") val stickers: List<StickerResponse>? = null
) : RemoteMapper<HobbyStickerHistoryEntity> {
    override fun toData(): HobbyStickerHistoryEntity = HobbyStickerHistoryEntity(
        hobbyId = hobbyId,
        durationSet = durationSet,
        activityRecordedToday = activityRecordedToday,
        currentPage = currentPage,
        totalPage = totalPage,
        pageSize = pageSize,
        totalStickerNum = totalStickerNum,
        hasPrevious = hasPrevious,
        hasNext = hasNext,
        stickers = stickers?.map { it.toData() } ?: emptyList()
    )
}

data class StickerResponse(
    @SerializedName("activityRecordId") val activityRecordId: Int,
    @SerializedName("sticker") val sticker: String?,
    @SerializedName("deleted") val deleted: Boolean?
) : RemoteMapper<StickerEntity?> {
    override fun toData(): StickerEntity = StickerEntity(
        activityRecordId = activityRecordId,
        sticker = sticker,
        deleted = deleted
    )
}