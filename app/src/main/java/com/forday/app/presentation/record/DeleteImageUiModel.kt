package com.forday.app.presentation.record

import com.forday.app.domain.model.DeleteS3ImageDomain


// UI 모델
data class DeleteImageUiModel(
    val isSuccess: Boolean,
    val message: String,
    val errorType: String? = null
)

// 변환 매퍼 (Domain -> UI)
fun DeleteS3ImageDomain.toPresentation(): DeleteImageUiModel {
    return DeleteImageUiModel(
        isSuccess = isSuccess,
        message = data.message,
        errorType = data.errorClassName
    )
}