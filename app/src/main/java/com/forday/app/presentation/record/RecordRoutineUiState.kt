package com.forday.app.presentation.record

import com.forday.app.core.designsystem.component.state.ErrorDataUiState
import com.forday.app.presentation.model.ModifyPostingUiModel

data class RecordRoutineUiState(
    // 1. 루틴 기록 관련 기본 정보 (메모, 스티커, 확장 여부 등)
    val recordDetail: RecordRoutineUiModel = RecordRoutineUiModel(),

    // 2. 이미지 업로드 관련 상태 (Presigned URL 및 업로드 진행률)
    val imageUploadState: PresignedUrlUiModel = PresignedUrlUiModel(emptyList()),

    // 3. 화면 전체 공통 상태
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isProcessComplete: Boolean = false, // 저장 완료 후 화면 이탈 처리용

    val routines: List<RoutineUiModel> = emptyList(),

    val modifyPostingUiModel: ModifyPostingUiModel? = null,  // 수정 ui 모델
    val errorData: ErrorDataUiState? = null,
)
