package com.forday.app.presentation.mypage.profilesetting

import com.forday.app.presentation.mypage.PresignedUrlUiModel

data class ProfileSettingUiState(
    val image: ImageUploadState = ImageUploadState(),
    val nickname: NicknameState = NicknameState(),
)

data class ImageUploadState(
    val uploadState: PresignedUrlUiModel = PresignedUrlUiModel(),
)

data class NicknameState(
    val checkMessage: String = "",
    val isChecked: Boolean? = null,
    val isCheckLoading: Boolean = false,
)
