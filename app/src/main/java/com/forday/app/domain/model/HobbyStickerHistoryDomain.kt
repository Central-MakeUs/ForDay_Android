package com.forday.app.domain.model

data class HobbyStickerHistoryDomain(
    val hobbyId: Int,
    val durationSet: Boolean,
    val activityRecordedToday: Boolean,
    val currentPage: Int,
    val totalPage: Int,
    val pageSize: Int,
    val totalStickerNum: Int,
    val hasPrevious: Boolean,
    val hasNext: Boolean,
    val stickers: List<StickerDomain> = emptyList()
)

data class StickerDomain(
    val activityRecordId: Int,
    val sticker: String?,
    val deleted: Boolean?
)