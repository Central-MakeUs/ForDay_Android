package com.forday.app.presentation.home

sealed interface HomeAction {
    // 데이터 로딩
    data class LoadHomeData(val hobbyId: Long? = null) : HomeAction
    data class SelectHobby(val hobbyId: Long?) : HomeAction
    data class SelectRoutine(val routineId: Int) : HomeAction

    // 스티커
    data object NextStickerPage : HomeAction
    data object PreviousStickerPage : HomeAction

    // AI 추천
    data class RequestAiRecommendation(val hobbyId: Long?) : HomeAction
    data class RequestAiRecommendationAgain(val hobbyId: Long?, val type: String? = "ALL") : HomeAction
    data class CreateRoutines(
        val hobbyId: Long?,
        val routineList: List<Pair<Boolean, String>>,
        val hobbyName: String? = null
    ) : HomeAction

    // AI 바텀시트 dismiss 시 홈 데이터 새로고침
    data class RefreshAfterAiDismiss(val hobbyId: Long?) : HomeAction

    // 로깅
    data class LogEvent(val event: String) : HomeAction
    data class LogAnalyticsEvent(val event: com.forday.app.core.logger.analytics.AnalyticsEvent) : HomeAction
}
