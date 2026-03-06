package com.forday.app.remote.model.response

import com.forday.app.data.model.BlockUserDataEntity
import com.forday.app.data.model.BlockUserEntity
import com.forday.app.remote.RemoteMapper
import com.google.gson.annotations.SerializedName

data class BlockUserResponse(
    @SerializedName("status") val status: Int?,
    @SerializedName("success") val success: Boolean?,
    @SerializedName("data") val data: BlockUserDataResponse?
) : RemoteMapper<BlockUserEntity> {
    override fun toData(): BlockUserEntity = BlockUserEntity(
        status = status ?: 0,
        success = success ?: false,
        data = data?.toData() ?: BlockUserDataEntity()
    )
}

data class BlockUserDataResponse(
    @SerializedName("message") val message: String?,
    @SerializedName("nickname") val nickname: String?
) : RemoteMapper<BlockUserDataEntity> {
    override fun toData(): BlockUserDataEntity = BlockUserDataEntity(
        message = message ?: "",
        nickname = nickname ?: ""
    )
}
