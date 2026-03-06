package com.forday.app.presentation.mypage.routinedetail

sealed interface RoutineDetailAction {
    // 상세 데이터 로드
    data class LoadDetail(val routineId: Int) : RoutineDetailAction

    // 스크랩 토글
    data class ToggleScrap(val routineId: Int, val isCurrentlyScraped: Boolean) : RoutineDetailAction

    // 반응 남기기
    data class ReactToPosting(
        val routineId: Int,
        val reactionType: String,
        val refreshUsers: Boolean
    ) : RoutineDetailAction

    // 반응 취소
    data class CancelReaction(
        val routineId: Int,
        val reactionType: String,
        val refreshUsers: Boolean
    ) : RoutineDetailAction

    // 반응 유저 목록 조회
    data class GetReactionUsers(val routineId: Int, val reactionType: String) : RoutineDetailAction

    // 대표사진 설정
    data class SetHobbyMainImage(val hobbyId: Long?, val recordId: Long?) : RoutineDetailAction

    // 게시물 삭제
    data class DeletePosting(val recordId: Long) : RoutineDetailAction

    // 공개범위 수정
    data class ModifyVisibility(val recordId: Int, val visibility: String) : RoutineDetailAction
}
