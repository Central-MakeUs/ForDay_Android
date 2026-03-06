package com.forday.app.presentation.home

/**
 * 스티커판 정보 UI 모델
 * 서버 응답: GetStickerInfoResDto
 */
data class StickerInfoUiModel(
    val hobbyId: Long,
    val durationSet: Boolean,                    // 목표 기간 설정 여부
    val activityRecordedToday: Boolean,          // 오늘 활동 기록 여부
    val currentPage: Int,                        // 현재 페이지 (1-based)
    val totalPage: Int,                          // 전체 페이지 수
    val pageSize: Int,                           // 한 페이지 스티커 수 (28)
    val totalStickerNum: Int = 0,                    // 전체 스티커 수
    val hasPrevious: Boolean,                    // 이전 페이지 존재 여부
    val hasNext: Boolean,                        // 다음 페이지 존재 여부
    val stickers: List<StickerUiModel>           // 현재 페이지 스티커 리스트
)

/**
 * 개별 스티커 UI 모델
 */
data class StickerUiModel(
    val activityRecordId: Int,
    val sticker: String?,  // "smile.jpg", "sad.jpg" 등
    val deleted: Boolean?
)

/**
 * 빈 StickerInfo (초기 상태)
 */
fun emptyStickerInfoUiModel() = StickerInfoUiModel(
    hobbyId = 0L,
    durationSet = false,
    activityRecordedToday = false,
    currentPage = 1,
    totalPage = 1,
    pageSize = 28,
    totalStickerNum = 50,
    hasPrevious = true,
    hasNext = true,
    stickers = emptyList()
)