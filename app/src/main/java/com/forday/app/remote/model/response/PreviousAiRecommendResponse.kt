package com.forday.app.remote.model.response

import com.forday.app.data.model.PreviousAiRecommendDataEntity
import com.forday.app.data.model.PreviousAiRecommendEntity
import com.forday.app.data.model.RecommendActivityItemEntity
import com.forday.app.remote.RemoteMapper
import com.google.gson.annotations.SerializedName

data class PreviousAiRecommendResponse(
    @SerializedName("status") val status: Int,
    @SerializedName("success") val isSuccess: Boolean,
    @SerializedName("data") val data: PreviousAiRecommendDataResponse
) : RemoteMapper<PreviousAiRecommendEntity> {
    override fun toData(): PreviousAiRecommendEntity = PreviousAiRecommendEntity(
        status = status,
        isSuccess = isSuccess,
        data = data.toData()
    )
}

data class PreviousAiRecommendDataResponse(
    @SerializedName("message") val message: String? = null,
    @SerializedName("hobbyId") val hobbyId: Long? = null,
    @SerializedName("hobbyName") val hobbyName: String? = null,
    @SerializedName("activityItems") val activityItems: List<RecommendActivityItemResponse>? = emptyList()
) : RemoteMapper<PreviousAiRecommendDataEntity> {
    override fun toData(): PreviousAiRecommendDataEntity = PreviousAiRecommendDataEntity(
        message = message ?: "",
        hobbyId = hobbyId ?: 0L,
        hobbyName = hobbyName ?: "",
        activityItems = activityItems?.map { it.toData() } ?: emptyList()
    )
}

data class RecommendActivityItemResponse(
    @SerializedName("itemId") val itemId: Int? = null,
    @SerializedName("content") val content: String? = null,
    @SerializedName("description") val description: String? = null
) : RemoteMapper<RecommendActivityItemEntity> {
    override fun toData(): RecommendActivityItemEntity = RecommendActivityItemEntity(
        itemId = itemId ?: 0,
        content = content ?: "",
        description = description ?: ""
    )
}
