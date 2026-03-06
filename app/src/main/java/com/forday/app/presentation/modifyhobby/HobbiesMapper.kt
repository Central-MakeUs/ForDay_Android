package com.forday.app.presentation.modifyhobby

import com.forday.app.domain.model.MyHobbyListDataDomain
import com.forday.app.domain.model.MyHobbyListItemDomain

// 매핑 함수들
fun MyHobbyListItemDomain.toPresentation() = HobbyItemUiModel(
    hobbyId = hobbyId,       // 취미 고유 식별자
    hobbyName = hobbyName,   // 취미 이름
    hobbyTimeMinutes = hobbyTimeMinutes,  // 1회 수행 기준 목표 시간(분)
    executionCount = executionCount,      // 해당 취미의 누적 실행 횟수
    goalDays = goalDays,                   // 설정된 목표 일수
    hobbyInfoId = hobbyInfoId,             // 취미카드 ID
    imageCode = imageCode                  // 취미 아이콘
)

// ViewModel에서 사용될 최종 변환 로직
fun MyHobbyListDataDomain.toPresentation() = ModifyHobbyUiState(
    currentHobbyStatus = currentHobbyStatus,
    inProgressHobbyCount = inProgressHobbyCount,
    archivedHobbyCount = archivedHobbyCount,
    hobbies = hobbies.map { it.toPresentation() }
)