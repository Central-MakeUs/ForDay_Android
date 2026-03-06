package com.forday.app.data.model

import com.forday.app.data.DataMapper
import com.forday.app.domain.model.*

data class HomeHobbyEntity(
    val status: Int,
    val isSuccess: Boolean,
    val data: HomeHobbyDataEntity?
) : DataMapper<HomeHobbyDomain> {
    override fun toDomain(): HomeHobbyDomain = HomeHobbyDomain(
        status = status,
        isSuccess = isSuccess,
        data = data?.toDomain()
    )
}

data class HomeHobbyDataEntity(
    val inProgressHobbies: List<InProgressHobbyEntity>,
    val activityPreview: ActivityPreviewEntity?,
    val greetingMessage: String, // 추가
    val userSummaryText: String, // 추가
    val recommendMessage: String, // 추가
    val aiCallRemaining: Boolean,
    val aiCallRemainingCount: Int?
) : DataMapper<HomeHobbyDataDomain> {
    override fun toDomain(): HomeHobbyDataDomain = HomeHobbyDataDomain(
        inProgressHobbies = inProgressHobbies.map { it.toDomain() },
        activityPreview = activityPreview?.toDomain(),
        greetingMessage = greetingMessage,
        userSummaryText = userSummaryText,
        recommendMessage = recommendMessage,
        aiCallRemaining = aiCallRemaining,
        aiCallRemainingCount = aiCallRemainingCount
    )
}

data class InProgressHobbyEntity(
    val hobbyId: Long,
    val hobbyName: String,
    val currentHobby: Boolean
) : DataMapper<InProgressHobbyDomain> {
    override fun toDomain(): InProgressHobbyDomain = InProgressHobbyDomain(
        hobbyId = hobbyId,
        hobbyName = hobbyName,
        currentHobby = currentHobby
    )
}

data class ActivityPreviewEntity(
    val activityId: Int,
    val content: String,
    val aiRecommended: Boolean
) : DataMapper<ActivityPreviewDomain> {
    override fun toDomain(): ActivityPreviewDomain = ActivityPreviewDomain(
        activityId = activityId,
        content = content,
        aiRecommended = aiRecommended
    )
}