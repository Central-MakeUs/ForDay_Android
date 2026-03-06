package com.forday.app.presentation.model

import com.forday.app.domain.model.ModifyPostingDomain

data class ModifyPostingUiModel(
    val message: String = "",
    val activityId: Int = 0,
    val displayContent: String = "",
    val stickerUrl: String = "",
    val memo: String = "",
    val imageUrl: String = "",
    val isPrivate: Boolean = false // 예시: 가시성 여부를 UI에서 쉽게 쓰기 위해 가공
)

// Domain -> UI 매퍼 확장 함수
fun ModifyPostingDomain.toModifyPostingUiModel(): ModifyPostingUiModel {
    return ModifyPostingUiModel(
        message = message,
        activityId = activityId,
        displayContent = content, // Domain의 content를 UI의 displayContent로 매핑
        stickerUrl = sticker,     // 이름의 명확성을 위해 sticker -> stickerUrl
        memo = memo,
        imageUrl = imageUrl,
        // 가시성 판단 로직 (문자열 비교 시 대소문자 무시로 더 안전하게)
        isPrivate = visibility.equals("PRIVATE", ignoreCase = true)
    )
}