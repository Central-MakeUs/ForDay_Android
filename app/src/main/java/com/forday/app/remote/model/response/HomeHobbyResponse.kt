package com.forday.app.remote.model.response

import com.forday.app.data.model.*
import com.forday.app.remote.RemoteMapper
import com.google.gson.annotations.SerializedName

data class HomeHobbyResponse(
    @SerializedName("status") val status: Int,
    @SerializedName("success") val isSuccess: Boolean,
    @SerializedName("data") val data: HomeHobbyData? = null
) : RemoteMapper<HomeHobbyEntity> {
    override fun toData(): HomeHobbyEntity = HomeHobbyEntity(
        status = status,
        isSuccess = isSuccess,
        data = data?.toData()
    )
}

data class HomeHobbyData(
    @SerializedName("inProgressHobbies") val inProgressHobbies: List<InProgressHobbyResponse>? = emptyList(),
    @SerializedName("activityPreview") val activityPreview: ActivityPreviewResponse? = null,
    @SerializedName("greetingMessage") val greetingMessage: String? = "",
    @SerializedName("userSummaryText") val userSummaryText: String? = "",
    @SerializedName("recommendMessage") val recommendMessage: String? = "",
    @SerializedName("aiCallRemaining") val aiCallRemaining: Boolean? = false,
    @SerializedName("aiCallRemainingCount") val aiCallRemainingCount: Int? = null
) : RemoteMapper<HomeHobbyDataEntity> {
    override fun toData(): HomeHobbyDataEntity = HomeHobbyDataEntity(
        inProgressHobbies = inProgressHobbies?.map { it.toData() } ?: emptyList(),
        activityPreview = activityPreview?.toData(),
        greetingMessage = greetingMessage ?: "",
        userSummaryText = userSummaryText ?: "",
        recommendMessage = recommendMessage ?: "",
        aiCallRemaining = aiCallRemaining ?: false,
        aiCallRemainingCount = aiCallRemainingCount
    )
}

data class InProgressHobbyResponse(
    @SerializedName("hobbyId") val hobbyId: Long,
    @SerializedName("hobbyName") val hobbyName: String,
    @SerializedName("currentHobby") val currentHobby: Boolean
) : RemoteMapper<InProgressHobbyEntity> {
    override fun toData(): InProgressHobbyEntity = InProgressHobbyEntity(
        hobbyId = hobbyId,
        hobbyName = hobbyName,
        currentHobby = currentHobby
    )
}

data class ActivityPreviewResponse(
    @SerializedName("activityId") val activityId: Int,
    @SerializedName("content") val content: String,
    @SerializedName("aiRecommended") val aiRecommended: Boolean
) : RemoteMapper<ActivityPreviewEntity> {
    override fun toData(): ActivityPreviewEntity = ActivityPreviewEntity(
        activityId = activityId,
        content = content,
        aiRecommended = aiRecommended
    )
}