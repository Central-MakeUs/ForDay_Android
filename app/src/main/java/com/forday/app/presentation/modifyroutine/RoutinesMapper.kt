package com.forday.app.presentation.modifyroutine

import com.forday.app.core.designsystem.component.state.ErrorDataUiState
import com.forday.app.domain.model.HobbyRoutineListDomain
import com.forday.app.domain.model.RoutineDataDomain

fun RoutineDataDomain.toUiModel(): RoutineUiModel {
    return RoutineUiModel(
        routineId = routineId.toLong(),
        content = content,
        isAiRecommended = isAiRecommended,
        isDeletable = isDeletable,
        collectedStickerNum = stickerCount
    )
}

/**
 * Domain List → UI Model List 변환
 */
fun List<RoutineDataDomain>.toUiModelList(): List<RoutineUiModel> {
    return this.map { it.toUiModel() }
}

/**
 * Domain Response → UI State 변환 (에러 처리 포함)
 */
fun HobbyRoutineListDomain.toPresentation(): RoutinesUiState {
    return if (isSuccess) {
        RoutinesUiState(
            isLoading = false,
            routines = data.routines.toUiModelList(),
            errorData = null,
        )
    } else {
        RoutinesUiState(
            isLoading = false,
            routines = emptyList(),
            errorData = ErrorDataUiState( // todo 확인 필요
                message = data.message,
                errorType = ErrorDataUiState.ErrorType.TYPE_RETRY,
            ),
        )
    }
}