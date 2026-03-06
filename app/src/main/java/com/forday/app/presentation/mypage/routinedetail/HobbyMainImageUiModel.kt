package com.forday.app.presentation.mypage.routinedetail

import com.forday.app.domain.model.HobbyMainImageDomain

data class HobbyMainImageUiModel(   // 취미 대표 이미지 설정 모델
    val hobbyId: Int? = null,
    val recordId: Long? = null,
    val imageUrl: String = "",
    val resultMessage: String = ""
)

fun HobbyMainImageDomain.toPresentation(): HobbyMainImageUiModel {
    return HobbyMainImageUiModel(
        hobbyId = this.hobbyId,
        recordId = this.recordId,
        imageUrl = this.imageUrl,
        resultMessage = this.message
    )
}