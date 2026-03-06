package com.forday.app.data.model

import com.forday.app.domain.model.HobbyMainImageDomain

data class HobbyMainImageEntity(
    val status: Int,
    val isSuccess: Boolean,
    val message: String?,
    val errorCode: String?,
    val data: HobbyMainImageDataEntity?
) {
    fun toDomain(): HobbyMainImageDomain = HobbyMainImageDomain(
        hobbyId = data?.hobbyId ?: 0,
        imageUrl = data?.coverImageUrl.orEmpty(),
        message = data?.message ?: message.orEmpty(),
        recordId = data?.recordId ?: 0
    )
}

data class HobbyMainImageDataEntity(
    val message: String?,
    val hobbyId: Int?,
    val recordId: Long?,
    val coverImageUrl: String?,
    val errorClassName: String?
)