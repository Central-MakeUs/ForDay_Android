package com.forday.app.presentation.mypage.routinedetail

import com.forday.app.presentation.mypage.routinedetail.screen.ReactionDetailUiModel

data class RoutineDetailUiState(
    val detail: DetailState = DetailState(),
    val reaction: ReactionState = ReactionState(),
)

data class DetailState(
    val routine: RoutineRecordDetailUiModel? = RoutineRecordDetailUiModel(),
    val nickname: String? = null,
)

data class ReactionState(
    val users: ReactionDetailUiModel = ReactionDetailUiModel(),
    val isScraped: Boolean? = null,
)
