package com.forday.app.presentation.home.model

import com.forday.app.core.designsystem.component.state.ErrorDataUiState
import com.forday.app.domain.model.AiRoutineItemDomain
import com.forday.app.presentation.home.StickerInfoUiModel
import com.forday.app.presentation.home.StickerUiModel

data class HomeState(
    val hobby: HobbyState = HobbyState(),
    val routine: RoutineState = RoutineState(),
    val sticker: StickerState = StickerState(),
    val aiRecommend: AiRecommendState = AiRecommendState(),
    val nickName: String? = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val errorData: ErrorDataUiState? = null,
) {
    // 하위 호환용 계산 프로퍼티 (기존 state.xxx 접근 유지)
    val inProgressHobbies get() = hobby.inProgressHobbies
    val greetingMessage get() = hobby.greetingMessage
    val userSummaryText get() = hobby.userSummaryText
    val recommendMessage get() = hobby.recommendMessage
    val routinePreview get() = routine.preview
    val routineList get() = routine.list
    val aiCallRemaining get() = routine.aiCallRemaining
    val aiCallRemainingCount get() = routine.aiCallRemainingCount
    val routineId get() = routine.createdRoutineId
    val stickers get() = sticker.stickers
    val stickerInfo get() = sticker.info
    val stickerCnt get() = sticker.totalCount
    val currentStickerPage get() = sticker.currentPage
    val aiRoutineList get() = aiRecommend.routines
    val aiRoutineLoaded get() = aiRecommend.loaded
    val aiCallCount get() = aiRecommend.callCount
    val recommendedText get() = aiRecommend.recommendedText

    val totalStickerPages: Int
        get() = maxOf(1, (stickerCnt + 27) / 28)

    val canGoPreviousPage: Boolean
        get() = currentStickerPage > 0

    val canGoNextPage: Boolean
        get() = currentStickerPage < totalStickerPages - 1
}

data class HobbyState(
    val inProgressHobbies: List<InProgressHobbyUiModel> = emptyList(),
    val greetingMessage: String = "",
    val userSummaryText: String = "",
    val recommendMessage: String = "",
    val hobbyList: List<MyHobbyUiModel> = emptyList(),
)

data class RoutineState(
    val preview: RoutinePreviewUiModel? = null,
    val list: List<RoutineUiModel> = emptyList(),
    val aiCallRemaining: Boolean? = null,
    val aiCallRemainingCount: Int? = null,
    val createdRoutineId: Int? = null,
)

data class StickerState(
    val stickers: List<StickerUiModel> = emptyList(),
    val info: StickerInfoUiModel? = null,
    val totalCount: Int = 0,
    val currentPage: Int = 0,
    val hasNextPage: Boolean = false,
)

data class AiRecommendState(
    val routines: List<AiRoutineItemState> = emptyList(),
    val loaded: Boolean = false,
    val callCount: Int? = null,
    val recommendedText: String = "",
)

data class AiRoutineItemState(
    val routineId: Int,
    val topic: String,
    val content: String,
    val description: String
)

fun AiRoutineItemDomain.toPresentation(): AiRoutineItemState {
    return AiRoutineItemState(
        routineId = this.routineId,
        topic = this.topic,
        content = this.content,
        description = this.description
    )
}
