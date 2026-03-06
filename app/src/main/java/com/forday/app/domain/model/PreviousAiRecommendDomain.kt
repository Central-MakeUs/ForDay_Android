package com.forday.app.domain.model

data class PreviousAiRecommendDomain(
    val status: Int,
    val isSuccess: Boolean,
    val data: PreviousAiRecommendDataDomain
)

data class PreviousAiRecommendDataDomain(
    val message: String,
    val hobbyId: Long,
    val hobbyName: String,
    val activityItems: List<RecommendActivityItemDomain>
)

data class RecommendActivityItemDomain(
    val itemId: Int,
    val content: String,
    val description: String
)
