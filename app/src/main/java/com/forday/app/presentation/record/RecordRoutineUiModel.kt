package com.forday.app.presentation.record

data class RecordRoutineUiModel(
    val routineRecordId: Int = 0,
    val routineContent: String = "",
    val stickerUrl: String = "",
    val memo: String = "",
    val imageUrl: String = "",
    val isExtensionRequired: Boolean = false, // 연장 팝업 노출 여부
    val successMessage: String = "",
    val routineList: List<RoutineUiModel> = emptyList(),  // 여기에 routineId 있음
)

data class RoutineUiModel(
    val routineId: Int,
    val content: String,
    val isAiRecommended: Boolean,
)
