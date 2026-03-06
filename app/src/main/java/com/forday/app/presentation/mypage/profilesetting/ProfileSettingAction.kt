package com.forday.app.presentation.mypage.profilesetting

import java.io.File

sealed interface ProfileSettingAction {
    // 이미지 업로드
    data class GetPresignedUrl(val images: List<Map<String, Any>>) : ProfileSettingAction
    data class UploadImageToS3(
        val file: File,
        val uploadUrl: String,
        val contentType: String,
        val order: Int
    ) : ProfileSettingAction
    data class DeleteS3Image(val imageUrl: String) : ProfileSettingAction
    data class SetProfileImage(val imageUrl: String? = null) : ProfileSettingAction

    // 닉네임
    data class CheckNicknameDuplicate(val nickname: String) : ProfileSettingAction
    data object ResetNicknameCheck : ProfileSettingAction
    data class RegisterNickname(val nickname: String?) : ProfileSettingAction
}
