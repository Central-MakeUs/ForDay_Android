package com.forday.app.remote.model.response

import com.forday.app.data.model.AiRecommendedDataEntity
import com.forday.app.data.model.AiRecommendedEntity
import com.forday.app.data.model.AiRoutineItemEntity
import com.forday.app.remote.RemoteMapper
import com.google.gson.annotations.SerializedName

data class AiRecommendedResponse(
    @SerializedName("status")
    val status: Int,
    @SerializedName("success")
    val isSuccess: Boolean,
    @SerializedName("data")
    val data: AiRecommendedData
): RemoteMapper<AiRecommendedEntity> {
    override fun toData(): AiRecommendedEntity {
        return AiRecommendedEntity(
            status = status,
            isSuccess = isSuccess,
            data = data.toData()
        )
    }
}

data class AiRecommendedData(
    @SerializedName("message")
    val message: String,
    @SerializedName("aiCallCount")
    val aiCallCount: Int,
    @SerializedName("aiCallLimit")
    val aiCallLimit: Int,
    @SerializedName("recommendedText")
    val recommendedText: String,
    @SerializedName("activities")
    val routines: List<AiRoutineItem>
): RemoteMapper<AiRecommendedDataEntity> {
    override fun toData(): AiRecommendedDataEntity {
        return AiRecommendedDataEntity(
            message = message,
            aiCallCount = aiCallCount,
            aiCallLimit = aiCallLimit,
            recommendedText = recommendedText,
            routines = routines.map { it.toData() }
        )
    }
}

data class AiRoutineItem(
    @SerializedName("activityId")
    val routineId: Int,
    @SerializedName("topic")
    val topic: String,
    @SerializedName("content")
    val content: String,
    @SerializedName("description")
    val description: String
): RemoteMapper<AiRoutineItemEntity> {
    override fun toData(): AiRoutineItemEntity {
        return AiRoutineItemEntity(
            routineId = routineId,
            topic = topic,
            content = content,
            description = description
        )
    }
}