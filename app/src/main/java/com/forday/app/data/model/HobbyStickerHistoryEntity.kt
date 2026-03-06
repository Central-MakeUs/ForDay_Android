package com.forday.app.data.model

import com.forday.app.data.DataMapper
import com.forday.app.domain.model.HobbyStickerHistoryDomain
import com.forday.app.domain.model.StickerDomain
import kotlin.collections.map

data class HobbyStickerHistoryEntity(
    val hobbyId: Int,
    val durationSet: Boolean,
    val activityRecordedToday: Boolean,
    val currentPage: Int,
    val totalPage: Int,
    val pageSize: Int,
    val totalStickerNum: Int,
    val hasPrevious: Boolean,
    val hasNext: Boolean,
    val stickers: List<StickerEntity> = emptyList()
) : DataMapper<HobbyStickerHistoryDomain> {
    override fun toDomain(): HobbyStickerHistoryDomain = HobbyStickerHistoryDomain(
        hobbyId = hobbyId,
        durationSet = durationSet,
        activityRecordedToday = activityRecordedToday,
        currentPage = currentPage,
        totalPage = totalPage,
        pageSize = pageSize,
        totalStickerNum = totalStickerNum,
        hasPrevious = hasPrevious,
        hasNext = hasNext,
        stickers = stickers.map { it.toDomain() }
    )
}

data class StickerEntity(
    val activityRecordId: Int,
    val sticker: String?,
    val deleted: Boolean?
) : DataMapper<StickerDomain> {
    override fun toDomain(): StickerDomain = StickerDomain(
        activityRecordId = activityRecordId,
        sticker = sticker,
        deleted = deleted
    )
}