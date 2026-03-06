package com.forday.app.core.logger.analytics

data class AnalyticsEvent(
    val id: String,
    val description: String,
    val params: Map<String, Any?> = emptyMap()
)

object AnalyticsEvents {
    // Login
    val LOGIN_SCREEN = AnalyticsEvent("login_screen", "로그인 화면 진입")
    val KAKAO_LOGIN_CLICK = AnalyticsEvent("kakao_login_click", "카카오 로그인 클릭")
    val GUEST_MODE_CLICK = AnalyticsEvent("guest_mode_click", "게스트 모드 클릭")

    // Select Hobby
    val SELECT_HOBBY_SCREEN = AnalyticsEvent("select_hobby_screen", "취미 선택 화면 진입")
    fun selectedHobbyCard(name: String) = AnalyticsEvent("selected_hobby_card", "취미 카드 선택 - $name")
    val CLICK_DIRECT_INPUT_HOBBY = AnalyticsEvent("click_direct_input_hobby_btn", "직접 입력 취미 버튼 클릭")
    fun hobbyUserCustom(text: String) = AnalyticsEvent("hobby_user_custom", "사용자 커스텀 취미 - $text")

    // Select Time
    val VIEW_HOBBY_TIME_SELECTION_SCREEN = AnalyticsEvent("view_hobby_time_selection_screen", "취미 시간 선택 화면 진입")
    fun selectedTime(minutes: Any) = AnalyticsEvent("selected_time", "선택된 시간 - ${minutes}분")
    val HOBBY_TIME_SELECTION_BACK = AnalyticsEvent("hobby_time_selection_back_click", "취미 시간 선택 뒤로가기")

    // Select Purpose
    val HOBBY_PURPOSE_SELECTION_SCREEN = AnalyticsEvent("hobby_purpose_selection_screen", "취미 목적 선택 화면 진입")
    val HOBBY_PURPOSE_SELECTION_BACK = AnalyticsEvent("hobby_purpose_selection_screen_back_click", "취미 목적 선택 뒤로가기")
    val HOBBY_PURPOSE_CUSTOM_CLICK = AnalyticsEvent("hobby_purpose_selection_screen_custom_purpose_click", "커스텀 목적 버튼 클릭")
    fun userCustomPurpose(text: String) = AnalyticsEvent("user_custom_purpose", "사용자 커스텀 목적 - $text")
    fun selectedPurpose(purposes: String) = AnalyticsEvent("selected_purpose", "선택된 목적 - $purposes")

    // Select Frequency
    val HOBBY_FREQUENCY_ENTRY = AnalyticsEvent("hobby_info_frequency_entry", "취미 빈도 설정 화면 진입")
    val HOBBY_FREQUENCY_BACK = AnalyticsEvent("hobby_frequency_back_click", "취미 빈도 뒤로가기")
    fun hobbyWeeklyCount(frequency: Any) = AnalyticsEvent("hobby_weekly_count", "주간 횟수 - $frequency")

    // Select Journey Days
    val HOBBY_JOURNEY_DATE_SCREEN = AnalyticsEvent("hobby_journey_date_screen", "취미 여정 날짜 화면 진입")
    val HOBBY_JOURNEY_DATE_BACK = AnalyticsEvent("hobby_journey_date_screen_back_click", "취미 여정 날짜 뒤로가기")
    val ONBOARDING_SUCCESS = AnalyticsEvent("onboarding_success", "온보딩 완료")
    fun selectedJourneyDate(mode: String) = AnalyticsEvent("selected_journey_date", "선택된 여정 기간 - $mode")

    // Nickname
    val NICKNAME_INPUT_SCREEN = AnalyticsEvent("nickname_direct_input_screen", "닉네임 입력 화면 진입")
    val NICKNAME_REGISTER_CLICK = AnalyticsEvent("nickname_register_click", "닉네임 등록 클릭")
    fun currentInputNickname(nickname: String) = AnalyticsEvent("current_input_nickname", "입력된 닉네임 - $nickname")

    // Home
    val HOME_SCREEN = AnalyticsEvent("home_screen", "홈 화면 진입")
    val HOME_ADD_HOBBY_ACTIVITY = AnalyticsEvent("home_screen_click_add_hobby_activity_btn", "취미 활동 추가 버튼 클릭")
    val HOME_SHOW_HOBBY_ACTIVITY_LIST = AnalyticsEvent("home_screen_click_show_hobby_activity_list_btn", "취미 활동 목록 보기 클릭")
    val HOME_CLICK_EMPTY_STICKER = AnalyticsEvent("home_screen_click_empty_sticker", "빈 스티커 클릭")

    // Sosik
    val SOSIK_SCREEN = AnalyticsEvent("sosik_screen", "소식 화면 진입")

    // Input Routine & AI Recommendation
    val HOBBY_INPUT_AI_RECOMMENDATIONS = AnalyticsEvent("hobby_input_view_ai_recommendations_click", "AI 추천 보기 클릭")
    fun finalHobbyActivity(activity: String) = AnalyticsEvent("final_hobby_activity", "최종 취미 활동 - $activity")
    val CREATE_HOBBY_CLICK = AnalyticsEvent("create_hobby_click", "취미 생성 클릭")
    val AI_RECOMMEND_SCREEN = AnalyticsEvent("ai_recommend_hobby_routine_screen", "AI 추천 루틴 화면 진입")
    val AI_RECOMMEND_BACK = AnalyticsEvent("ai_recommend_hobby_routine_screen_back_btn_click", "AI 추천 루틴 뒤로가기")
    fun aiRecommendSelectedRoutine(content: String) = AnalyticsEvent("ai_recommend_selected_routine", "AI 추천 루틴 선택 - $content")
    fun aiRetryClick(count: Any) = AnalyticsEvent("ai_activity_retry_click", "AI 활동 재시도 - ${count}회")
    val CLICK_AI_RECOMMENDATION_NEXT = AnalyticsEvent("click_ai_recommendation_next", "AI 추천 다음 클릭")

    // Record & Modify
    val RECORD_ROUTINE_SCREEN = AnalyticsEvent("record_routine_screen", "루틴 기록 화면 진입")
    val MODIFY_HOBBY_SCREEN = AnalyticsEvent("modify_hobby_screen", "취미 수정 화면 진입")

    // AI Recommendation (with params)
    fun aiRecommendationShown(hobbyName: String?, recommendationCount: Int?) = AnalyticsEvent(
        id = "ai_recommendation_shown",
        description = "AI 추천 화면 로드 - $hobbyName",
        params = mapOf(
            "hobby_name" to hobbyName,
            "recommendation_count" to recommendationCount
        )
    )

    fun aiRecommendationClicked(hobbyName: String?, activityName: String, position: Int) = AnalyticsEvent(
        id = "ai_recommendation_clicked",
        description = "추천 활동 클릭 - $activityName",
        params = mapOf(
            "hobby_name" to hobbyName,
            "activity_name" to activityName,
            "position" to position
        )
    )

    fun activityAddEntryClicked(entryPoint: String, hobbyName: String?) = AnalyticsEvent(
        id = "activity_add_entry_clicked",
        description = "활동 추가 버튼 클릭 - $entryPoint",
        params = mapOf(
            "entry_point" to entryPoint,
            "hobby_name" to hobbyName
        )
    )

    fun activityAdded(entryPoint: String, source: String, hobbyName: String?, activityName: String) = AnalyticsEvent(
        id = "activity_added",
        description = "활동 추가 완료 - $activityName",
        params = mapOf(
            "entry_point" to entryPoint,
            "source" to source,
            "hobby_name" to hobbyName,
            "activity_name" to activityName
        )
    )

    fun recordEntryClicked(entryPoint: String, hobbyName: String?, activityName: String?) = AnalyticsEvent(
        id = "record_entry_clicked",
        description = "기록 작성 시작 - $entryPoint",
        params = mapOf(
            "entry_point" to entryPoint,
            "hobby_name" to hobbyName,
            "activity_name" to activityName
        )
    )

    fun recordCreated(entryPoint: String, hobbyName: String?, activityName: String, hasPhoto: Boolean, hasMemo: Boolean) = AnalyticsEvent(
        id = "record_created",
        description = "기록 작성 완료 - $activityName",
        params = mapOf(
            "entry_point" to entryPoint,
            "hobby_name" to hobbyName,
            "activity_name" to activityName,
            "has_photo" to hasPhoto,
            "has_memo" to hasMemo
        )
    )
}
