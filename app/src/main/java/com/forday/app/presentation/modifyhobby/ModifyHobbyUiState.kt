package com.forday.app.presentation.modifyhobby

import com.forday.app.core.designsystem.component.state.ErrorDataUiState

// UI 아이템 모델 (이름 변경)
data class HobbyItemUiModel(
    val hobbyId: Int,
    val hobbyName: String,
    val hobbyTimeMinutes: Int,
    val executionCount: Int,
    val goalDays: Int,
    val hobbyInfoId: Int?,
    val imageCode: String
)

// UI 전체 상태
data class ModifyHobbyUiState(
    val isLoading: Boolean = false,
    val currentHobbyStatus: String? = null,  // 현재 선택된 취미의 상태
    val inProgressHobbyCount: Int = 0,       // 진행 중(IN_PROGRESS) 상태의 취미 개수
    val archivedHobbyCount: Int = 0,         // 보관(ARCHIVED) 상태의 취미 개수
    val hobbies: List<HobbyItemUiModel> = emptyList(),  // 취미 목록
    val showHobbyLimitDialog: Boolean = false,  // 진행중 취미 최대 초과 다이얼로그
    val toastMessage: String? = null,
    val toastTargetTab: String? = null,  // "IN_PROGRESS" or "ARCHIVED"
    val errorData: ErrorDataUiState? = null,
)

