package com.forday.app.data.model

import com.forday.app.data.DataMapper
import com.forday.app.domain.model.AiRecommendedDataDomain
import com.forday.app.domain.model.AiRecommendedDomain
import com.forday.app.domain.model.AiRoutineItemDomain


data class AiRecommendedEntity(
    val status: Int,
    val isSuccess: Boolean,
    val data: AiRecommendedDataEntity
) : DataMapper<AiRecommendedDomain> {
    override fun toDomain(): AiRecommendedDomain {
        return AiRecommendedDomain(
            status = status,
            isSuccess = isSuccess,
            data = data.toDomain()
        )
    }
}


data class AiRecommendedDataEntity(
    val message: String,
    val aiCallCount: Int,
    val aiCallLimit: Int,
    val recommendedText: String,
    val routines: List<AiRoutineItemEntity>
) : DataMapper<AiRecommendedDataDomain> {
    override fun toDomain(): AiRecommendedDataDomain {
        return AiRecommendedDataDomain(
            message = message,
            aiCallCount = aiCallCount,
            aiCallLimit = aiCallLimit,
            recommendedText = recommendedText,
            routines = routines.map { it.toDomain() }
        )
    }
}


data class AiRoutineItemEntity(
    val routineId: Int,
    val topic: String,
    val content: String,
    val description: String
) : DataMapper<AiRoutineItemDomain> {
    override fun toDomain(): AiRoutineItemDomain {
        return AiRoutineItemDomain(
            routineId = routineId,
            topic = topic,
            content = content,
            description = description
        )
    }
}