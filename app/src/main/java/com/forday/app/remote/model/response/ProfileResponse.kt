package com.forday.app.remote.model.response

import com.forday.app.data.model.ProfileDataEntity
import com.forday.app.data.model.ProfileEntity
import com.forday.app.remote.RemoteMapper
import com.google.gson.annotations.SerializedName

data class ProfileResponse(
    @SerializedName("status")
    val status: Int,
    @SerializedName("success")
    val isSuccess: Boolean,
    @SerializedName("data")
    val data: ProfileDataResponse
) : RemoteMapper<ProfileEntity> {
    override fun toData(): ProfileEntity {
        return ProfileEntity(
            status = status,
            isSuccess = isSuccess,
            data = data.toData()
        )
    }
}

data class ProfileDataResponse(
    @SerializedName("profileImageUrl")
    val profileImageUrl: String?,
    @SerializedName("nickname")
    val nickname: String?,
    @SerializedName("totalCollectedStickerCount")
    val totalCollectedStickerCount: Int?,
    // 예외 상황 대응을 위한 공통 필드
    @SerializedName("message")
    val message: String?,
    @SerializedName("errorClassName")
    val errorClassName: String?
) : RemoteMapper<ProfileDataEntity> {
    override fun toData(): ProfileDataEntity {
        return ProfileDataEntity(
            profileImageUrl = profileImageUrl ?: "",
            nickname = nickname ?: "사용자",
            totalCollectedStickerCount = totalCollectedStickerCount ?: 0,
            message = message ?: "",
            errorClassName = errorClassName ?: ""
        )
    }
}