package com.forday.app.presentation.record

// 개별 이미지 아이템의 UI 상태
data class PresignedUrlItemUiModel(
    val uploadUrl: String,
    val fileUrl: String,
    val order: Int,
    // UI 전용 상태 값 (초기값 설정)
    val isUploading: Boolean = false,
    val isSuccess: Boolean = false
)

// 전체 이미지 리스트를 담는 UI 상태
data class PresignedUrlUiModel(
    val images: List<PresignedUrlItemUiModel>
)