package com.forday.app.presentation.record

import com.forday.app.domain.model.PresignedUrlDataDomain
import com.forday.app.domain.model.PresignedUrlItemDomain
import com.forday.app.domain.model.RoutineListItemDomain
import com.forday.app.domain.model.WriteRoutineDataDomain

fun WriteRoutineDataDomain.toPresentation(): RecordRoutineUiModel {
    return RecordRoutineUiModel(
        routineRecordId = routineRecordId,
        routineContent = routineContent,
        stickerUrl = sticker, // 서버의 sticker 필드를 URL로 매핑
        memo = memo,
        imageUrl = imageUrl,
        isExtensionRequired = extensionCheckRequired,
        successMessage = message
    )
}

fun PresignedUrlItemDomain.toUiModel(): PresignedUrlItemUiModel {
    return PresignedUrlItemUiModel(
        uploadUrl = this.uploadUrl,
        fileUrl = this.fileUrl,
        order = this.order,
        isUploading = false, // 초기 상태
        isSuccess = false
    )
}

fun RoutineListItemDomain.toUiModel(): RoutineUiModel {  // 드롭 다운용 특정 취미 활동 목록 조회
    return RoutineUiModel(
        routineId = routineId,
        content = content,
        isAiRecommended = aiRecommended,
    )
}

// 리스트 단위 변환
fun PresignedUrlDataDomain.toUiModel(): PresignedUrlUiModel {
    return PresignedUrlUiModel(
        images = this.images.map { it.toUiModel() }
    )
}