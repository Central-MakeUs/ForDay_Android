package com.forday.app.data.model

import com.forday.app.domain.model.ProfileDomain

data class ProfileEntity(
    val status: Int,
    val isSuccess: Boolean,
    val data: ProfileDataEntity
) {
    fun toDomain(): ProfileDomain {
        return ProfileDomain(
            status = status,
            isSuccess = isSuccess,
            imageUrl = data.profileImageUrl,
            nickname = data.nickname,
            stickerCount = data.totalCollectedStickerCount,
            message = data.message,
            errorClassName = data.errorClassName
        )
    }
}

data class ProfileDataEntity(
    val profileImageUrl: String?,
    val nickname: String?,
    val totalCollectedStickerCount: Int?,
    val message: String?,
    val errorClassName: String?
)