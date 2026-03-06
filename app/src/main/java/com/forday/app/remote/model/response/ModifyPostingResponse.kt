package com.forday.app.remote.model.response

import com.forday.app.data.model.ModifyPostingDataEntity
import com.forday.app.data.model.ModifyPostingEntity
import com.forday.app.remote.RemoteMapper
import kotlinx.serialization.Serializable

@Serializable
data class ModifyPostingResponse(
    val status: Int,
    val success: Boolean,
    val data: ModifyPostingDataResponse
) : RemoteMapper<ModifyPostingEntity> {
    override fun toData(): ModifyPostingEntity {
        return ModifyPostingEntity(
            status = status,
            isSuccess = success,
            data = data.toData()
        )
    }
}

@Serializable
data class ModifyPostingDataResponse(
    val message: String,
    val activityId: Int,
    val activityContent: String,
    val sticker: String,
    val memo: String,
    val imageUrl: String,
    val visibility: String
) : RemoteMapper<ModifyPostingDataEntity> {
    override fun toData(): ModifyPostingDataEntity {
        return ModifyPostingDataEntity(
            message = message,
            activityId = activityId,
            activityContent = activityContent,
            sticker = sticker,
            memo = memo,
            imageUrl = imageUrl,
            visibility = visibility
        )
    }
}