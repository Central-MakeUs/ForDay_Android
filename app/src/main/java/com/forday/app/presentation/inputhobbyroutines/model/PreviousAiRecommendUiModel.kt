package com.forday.app.presentation.inputhobbyroutines.model

import com.forday.app.domain.model.PreviousAiRecommendDataDomain
import com.forday.app.domain.model.RecommendActivityItemDomain

data class PreviousAiRecommendUiModel(
    val message: String,
    val hobbyId: Long,
    val hobbyName: String,
    val activityItems: List<RecommendActivityItemUiModel>
)

data class RecommendActivityItemUiModel(
    val itemId: Int,
    val content: String,
    val description: String
)

fun PreviousAiRecommendDataDomain.toPresentation(): PreviousAiRecommendUiModel =
    PreviousAiRecommendUiModel(
        message = message,
        hobbyId = hobbyId,
        hobbyName = hobbyName,
        activityItems = activityItems.map { it.toPresentation() }
    )

fun RecommendActivityItemDomain.toPresentation(): RecommendActivityItemUiModel =
    RecommendActivityItemUiModel(
        itemId = itemId,
        content = content,
        description = description
    )
