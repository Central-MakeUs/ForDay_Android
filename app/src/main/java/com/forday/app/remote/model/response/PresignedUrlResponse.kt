package com.forday.app.remote.model.response

import com.forday.app.data.model.PresignedUrlDataEntity
import com.forday.app.data.model.PresignedUrlEntity
import com.forday.app.data.model.PresignedUrlItemEntity
import com.forday.app.remote.RemoteMapper
import com.google.gson.annotations.SerializedName

data class PresignedUrlResponse(
    @SerializedName("status") val status: Int,
    @SerializedName("success") val success: Boolean,
    @SerializedName("data") val data: List<PresignedUrlItem>  // ✅ List로 직접 받기
) : RemoteMapper<PresignedUrlEntity> {
    override fun toData(): PresignedUrlEntity {
        return PresignedUrlEntity(
            status = status,
            success = success,
            data = PresignedUrlDataEntity(
                images = data.map { it.toData() }  // ✅ List를 그대로 매핑
            )
        )
    }
}

data class PresignedUrlData(
    @SerializedName("images") val images: List<PresignedUrlItem>
) : RemoteMapper<PresignedUrlDataEntity> {
    override fun toData(): PresignedUrlDataEntity {
        return PresignedUrlDataEntity(
            images = images.map { it.toData() }
        )
    }
}

data class PresignedUrlItem(
    @SerializedName("uploadUrl") val uploadUrl: String,
    @SerializedName("fileUrl") val fileUrl: String,
    @SerializedName("order") val order: Int
) : RemoteMapper<PresignedUrlItemEntity> {
    override fun toData(): PresignedUrlItemEntity {
        return PresignedUrlItemEntity(
            uploadUrl = uploadUrl,
            fileUrl = fileUrl,
            order = order
        )
    }
}