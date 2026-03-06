package com.forday.app.domain.model

data class HomeHobbyDomain(
    val status: Int,
    val isSuccess: Boolean,
    val data: HomeHobbyDataDomain?
)

data class HomeHobbyDataDomain(
    val inProgressHobbies: List<InProgressHobbyDomain>,
    val activityPreview: ActivityPreviewDomain?,
    val greetingMessage: String, // 추가: "반가워요, 몽실님! 👋"
    val userSummaryText: String, // 추가: 요약 텍스트
    val recommendMessage: String, // 추가: "포데이 AI가 알맞은 취미활동을 추천해드려요"
    val aiCallRemaining: Boolean,
    val aiCallRemainingCount: Int?
)

data class InProgressHobbyDomain(
    val hobbyId: Long,
    val hobbyName: String,
    val currentHobby: Boolean
)

data class ActivityPreviewDomain(
    val activityId: Int,
    val content: String,
    val aiRecommended: Boolean
)