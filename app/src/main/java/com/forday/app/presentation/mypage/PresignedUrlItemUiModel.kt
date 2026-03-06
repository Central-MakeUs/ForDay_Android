package com.forday.app.presentation.mypage

import com.forday.app.domain.model.PresignedUrlDataDomain
import com.forday.app.domain.model.PresignedUrlItemDomain
import kotlin.collections.firstOrNull

data class PresignedUrlUiModel(
    val uploadUrl: String? = null,
    val fileUrl: String? = null,
    // UI 전용 상태 값 (초기값 설정)
    val isUploading: Boolean = false,
    val isSuccess: Boolean = false
)

fun PresignedUrlDataDomain.toPresentation(): PresignedUrlUiModel =
    images.firstOrNull()?.toPresentation() ?: PresignedUrlUiModel()

private fun PresignedUrlItemDomain.toPresentation(): PresignedUrlUiModel =
    PresignedUrlUiModel(
        uploadUrl = uploadUrl,
        fileUrl = fileUrl
    )