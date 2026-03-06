package com.forday.app.data.model

import com.forday.app.data.DataMapper
import com.forday.app.domain.model.PreviousAiRecommendDataDomain
import com.forday.app.domain.model.PreviousAiRecommendDomain
import com.forday.app.domain.model.RecommendActivityItemDomain

data class PreviousAiRecommendEntity(
    val status: Int,
    val isSuccess: Boolean,
    val data: PreviousAiRecommendDataEntity
) : DataMapper<PreviousAiRecommendDomain> {
    override fun toDomain(): PreviousAiRecommendDomain = PreviousAiRecommendDomain(
        status = status,
        isSuccess = isSuccess,
        data = data.toDomain()
    )
}

data class PreviousAiRecommendDataEntity(
    val message: String,
    val hobbyId: Long,
    val hobbyName: String,
    val activityItems: List<RecommendActivityItemEntity>
) : DataMapper<PreviousAiRecommendDataDomain> {
    override fun toDomain(): PreviousAiRecommendDataDomain = PreviousAiRecommendDataDomain(
        message = message,
        hobbyId = hobbyId,
        hobbyName = hobbyName,
        activityItems = activityItems.map { it.toDomain() }
    )
}

data class RecommendActivityItemEntity(
    val itemId: Int,
    val content: String,
    val description: String
) : DataMapper<RecommendActivityItemDomain> {
    override fun toDomain(): RecommendActivityItemDomain = RecommendActivityItemDomain(
        itemId = itemId,
        content = content,
        description = description
    )
}
