package com.forday.app.presentation.home.model

import com.forday.app.domain.model.HobbyStickerHistoryDomain
import com.forday.app.domain.model.HomeHobbyDataDomain
import com.forday.app.domain.model.MyHobbyListItemDomain
import com.forday.app.domain.model.RoutineListItemDomain
import com.forday.app.domain.model.StickerDomain
import com.forday.app.presentation.home.StickerInfoUiModel
import com.forday.app.presentation.home.StickerUiModel

fun HomeHobbyDataDomain.toPresentation(): HomeHobbyUiModel {
    return HomeHobbyUiModel(
        // 1. 진행 중인 취미 리스트 매핑
        inProgressHobbies = inProgressHobbies.map {
            InProgressHobbyUiModel(
                hobbyId = it.hobbyId,
                name = it.hobbyName,
                isCurrent = it.currentHobby
            )
        },
        // 2. 루틴 프리뷰 매핑 (null 안전성 처리)
        routinePreview = activityPreview?.let {
            RoutinePreviewUiModel(
                routineId = it.activityId,
                content = it.content,
                isAiRecommended = it.aiRecommended
            )
        },
        // 3. 새로운 메시지 필드들 매핑
        greetingMessage = greetingMessage,
        userSummaryText = userSummaryText,
        recommendMessage = recommendMessage,
        // 4. AI 호출 남은 횟수 상태
        aiCallRemaining = aiCallRemaining,
        aiCallRemainingCount = aiCallRemainingCount
    )
}

fun RoutineListItemDomain.toPresentation(): RoutineUiModel {
    return RoutineUiModel(
        routineId = this.routineId,
        content = this.content,
        isAiRecommended = this.aiRecommended
    )
}

fun MyHobbyListItemDomain.toPresentation(): MyHobbyUiModel {
    return MyHobbyUiModel(
        id = hobbyId,
        title = hobbyName,
        time = hobbyTimeMinutes,
        progressCount = executionCount,
        totalDays = goalDays
    )
}

//fun WriteRoutineDataDomain.toPresentation(): WriteRoutineUiModel {
//    return WriteRoutineUiModel(
//        id = this.activityRecordId,
//        stickerImg = this.sticker,
//        content = this.activityContent
//    )
//}

// 스티커판 조회 todo 다시 복구해야 함 아래꺼 지우고
//fun HobbyStickerHistoryDomain.toPresentation(): HobbyStickerHistoryUiModel {
//    return HobbyStickerHistoryUiModel(
//        hobbyId = hobbyId,
//        isDurationSet = durationSet,
//        isRecordedToday = activityRecordedToday,
//        totalStickerCount = totalStickerNum,
//        stickers = stickers.map {
//            StickerUiModel(recordId = it.activityRecordId, stickerUrl = it.sticker)
//        },
//        hasNext = hasNext,
//        currentPage = currentPage
//    )
//}

fun HobbyStickerHistoryDomain.toPresentation(): StickerInfoUiModel {
    return StickerInfoUiModel(
        hobbyId = hobbyId.toLong(),
        durationSet = durationSet,
        activityRecordedToday = activityRecordedToday,
        currentPage = currentPage,
        totalPage = totalPage,
        pageSize = pageSize,
        totalStickerNum = totalStickerNum,
        hasPrevious = hasPrevious,
        hasNext = hasNext,
        stickers = stickers.map { it.toPresentation() }
    )
}

fun StickerDomain.toPresentation() = StickerUiModel(
    activityRecordId = this.activityRecordId,
    sticker = this.sticker,
    deleted = this.deleted
)