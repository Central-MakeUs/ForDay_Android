package com.forday.app.presentation.modifyroutine

import com.forday.app.core.designsystem.component.state.ErrorDataUiState

data class RoutinesUiState(
    val isLoading: Boolean = false,
    val routines: List<RoutineUiModel> = emptyList(),
    val errorData: ErrorDataUiState? = null,
)

data class RoutineUiModel(
    val routineId: Long,
    val content: String,
    val isAiRecommended: Boolean,
    val isDeletable: Boolean,
    val collectedStickerNum: Int,
)
