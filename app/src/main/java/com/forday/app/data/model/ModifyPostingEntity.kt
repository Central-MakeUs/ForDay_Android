package com.forday.app.data.model

import com.forday.app.domain.model.ModifyPostingDomain

data class ModifyPostingEntity(
    val status: Int,
    val isSuccess: Boolean,
    val data: ModifyPostingDataEntity
) {
    fun toDomain(): ModifyPostingDomain {
        return ModifyPostingDomain(
            message = data.message,
            activityId = data.activityId,
            content = data.activityContent,
            sticker = data.sticker,
            memo = data.memo,
            imageUrl = data.imageUrl,
            visibility = data.visibility
        )
    }
}

data class ModifyPostingDataEntity(
    val message: String,
    val activityId: Int,
    val activityContent: String,
    val sticker: String,
    val memo: String,
    val imageUrl: String,
    val visibility: String
)