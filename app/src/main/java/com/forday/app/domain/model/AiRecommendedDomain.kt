package com.forday.app.domain.model

data class AiRecommendedDomain(
    val status: Int,
    val isSuccess: Boolean,
    val data: AiRecommendedDataDomain
)

data class AiRecommendedDataDomain(
    val message: String,
    val aiCallCount: Int,
    val aiCallLimit: Int,
    val recommendedText: String,
    val routines: List<AiRoutineItemDomain>
)

data class AiRoutineItemDomain(
    val routineId: Int,
    val topic: String,
    val content: String,
    val description: String
)