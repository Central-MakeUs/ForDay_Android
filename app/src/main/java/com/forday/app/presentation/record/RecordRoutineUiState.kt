package com.forday.app.presentation.record

import com.forday.app.core.designsystem.component.state.ErrorDataUiState

data class RecordRoutineUiState(
    // 1. 루틴 기록 관련 기본 정보 (메모, 스티커, 확장 여부 등)
    val recordDetail: RecordRoutineUiModel = RecordRoutineUiModel(),

    // 2. 이미지 업로드 관련 상태 (Presigned URL 및 업로드 진행률)
    val imageUploadState: PresignedUrlUiModel = PresignedUrlUiModel(emptyList()),

    // 3. 에러 상태
    val errorData: ErrorDataUiState? = null,
)
