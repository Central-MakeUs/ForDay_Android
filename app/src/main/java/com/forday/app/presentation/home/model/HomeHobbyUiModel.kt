package com.forday.app.presentation.home.model

data class HomeHobbyUiModel(
    val inProgressHobbies: List<InProgressHobbyUiModel>,
    val routinePreview: RoutinePreviewUiModel?,
    val greetingMessage: String,   // 추가
    val userSummaryText: String,   // 추가
    val recommendMessage: String,  // 추가
    val aiCallRemaining: Boolean,
    val aiCallRemainingCount: Int?
)

data class InProgressHobbyUiModel(
    val hobbyId: Long? = null,
    val name: String,
    val isCurrent: Boolean
)

data class RoutinePreviewUiModel(
    val routineId: Int,
    val content: String,
    val isAiRecommended: Boolean
)