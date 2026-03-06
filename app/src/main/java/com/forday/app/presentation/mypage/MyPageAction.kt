package com.forday.app.presentation.mypage

import android.content.Context

sealed interface MyPageAction {
    // 초기 데이터 로드
    data class LoadInitialData(val userId: String?) : MyPageAction

    // Pull-to-Refresh
    data class Refresh(
        val selectedTab: Int,
        val selectedHobbyIds: List<Int?>,
        val userId: String?
    ) : MyPageAction

    // 탭 전환 시 스크랩 로드
    data class SelectTab(val tab: Int, val userId: String?) : MyPageAction

    // 취미 필터 변경
    data class FilterByHobby(val hobbyIds: List<Int?>, val userId: String?) : MyPageAction

    // 게스트 바텀시트 표시 완료
    data object MarkGuestBottomSheetShown : MyPageAction

    // 카카오 로그인
    data class LoginWithKakao(val context: Context, val socialType: String) : MyPageAction

    // 사용자 차단
    data class BlockUser(val userId: String, val nickname: String) : MyPageAction

    // 차단 성공 플래그 리셋
    data object ResetBlockUserSuccess : MyPageAction
}
