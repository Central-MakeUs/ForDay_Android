package com.forday.app.presentation.sosik

import android.content.Context

sealed interface SosikAction {
    // 데이터 로드
    data class FetchRoutineList(
        val hobbyId: Long? = null,
        val lastRecordId: Long? = null,
        val size: Long? = 20L,
        val keyword: String? = null,
        val storyFilterType: String? = "ALL"
    ) : SosikAction
    data object LoadMore : SosikAction
    data class SelectTab(val index: Int) : SosikAction

    // 유저 정보
    data object GetUserLoginInfo : SosikAction
    data object MarkGuestBottomSheetShown : SosikAction

    // 리액션
    data class ToggleAwesome(val recordId: Long) : SosikAction
    data class ReactionToPosting(val recordId: Int, val reactionType: String) : SosikAction
    data class CancelReaction(val recordId: Int, val reactionType: String) : SosikAction

    // 로그인
    data class LoginWithKakao(val context: Context, val socialType: String) : SosikAction
}
