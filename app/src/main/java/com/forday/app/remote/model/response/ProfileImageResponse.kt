package com.forday.app.remote.model.response

import com.forday.app.data.model.ProfileImageDataEntity
import com.forday.app.data.model.ProfileImageEntity
import com.forday.app.remote.RemoteMapper
import com.google.gson.annotations.SerializedName

data class ProfileImageResponse(
    @SerializedName("status")
    val status: Int,
    @SerializedName("success")
    val isSuccess: Boolean,
    @SerializedName("data")
    val data: ProfileImageDataResponse
) : RemoteMapper<ProfileImageEntity> {
    override fun toData(): ProfileImageEntity {
        return ProfileImageEntity(
            status = status,
            isSuccess = isSuccess,
            data = data.toData()
        )
    }
}

data class ProfileImageDataResponse(
    @SerializedName("message")
    val message: String,
    @SerializedName("profileImageUrl")
    val profileImageUrl: String?,
    @SerializedName("errorClassName")
    val errorClassName: String?
) : RemoteMapper<ProfileImageDataEntity> {
    override fun toData(): ProfileImageDataEntity {
        return ProfileImageDataEntity(
            message = message,
            profileImageUrl = profileImageUrl ?: "",
            errorClassName = errorClassName ?: ""
        )
    }
}