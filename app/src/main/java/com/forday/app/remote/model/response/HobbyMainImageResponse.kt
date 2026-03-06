package com.forday.app.remote.model.response

import com.forday.app.data.model.HobbyMainImageEntity
import com.forday.app.data.model.HobbyMainImageDataEntity
import com.forday.app.remote.RemoteMapper
import com.google.gson.annotations.SerializedName

data class HobbyMainImageResponse(
    @SerializedName("status") val status: Int,
    @SerializedName("success") val isSuccess: Boolean,
    @SerializedName("message") val message: String? = null, // 400 에러용
    @SerializedName("errorCode") val errorCode: String? = null, // 400 에러용
    @SerializedName("data") val data: HobbyMainImageData? = null
) : RemoteMapper<HobbyMainImageEntity> {
    override fun toData(): HobbyMainImageEntity = HobbyMainImageEntity(
        status = status,
        isSuccess = isSuccess,
        message = message,
        errorCode = errorCode,
        data = data?.toData()
    )
}

data class HobbyMainImageData(
    @SerializedName("message") val message: String? = null,
    @SerializedName("hobbyId") val hobbyId: Int? = null,
    @SerializedName("recordId") val recordId: Long? = null,
    @SerializedName("coverImageUrl") val coverImageUrl: String? = null,
    @SerializedName("errorClassName") val errorClassName: String? = null // 403 에러용
) : RemoteMapper<HobbyMainImageDataEntity> {
    override fun toData(): HobbyMainImageDataEntity = HobbyMainImageDataEntity(
        message = message,
        hobbyId = hobbyId,
        recordId = recordId,
        coverImageUrl = coverImageUrl,
        errorClassName = errorClassName
    )
}