package com.forday.app.data.model

import com.forday.app.domain.model.ProfileImageDomain

data class ProfileImageEntity(
    val status: Int,
    val isSuccess: Boolean,
    val data: ProfileImageDataEntity
) {
    fun toDomain(): ProfileImageDomain {
        return ProfileImageDomain(
            status = status,
            isSuccess = isSuccess,
            message = data.message,
            imageUrl = data.profileImageUrl,
            errorClassName = data.errorClassName
        )
    }
}

data class ProfileImageDataEntity(
    val message: String,
    val profileImageUrl: String,
    val errorClassName: String
)