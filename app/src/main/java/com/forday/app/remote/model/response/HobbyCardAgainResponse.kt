package com.forday.app.remote.model.response

import com.forday.app.data.model.HobbyCardAgainEntity
import com.forday.app.data.model.HobbyItemEntity
import com.forday.app.remote.RemoteMapper
import com.google.gson.annotations.SerializedName

/**
 * 취미 카드 다시 불러오기 API 응답
 */
data class HobbyCardAgainResponse(
    @SerializedName("status")
    val status: Int,
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("data")
    val data: HobbyCardAgainData
) : RemoteMapper<HobbyCardAgainEntity> {
    override fun toData(): HobbyCardAgainEntity {
        return HobbyCardAgainEntity(
            hobbies = data.hobbyInfo.map { it.toData() }
        )
    }
}

data class HobbyCardAgainData(
    @SerializedName("hobbyInfo")
    val hobbyInfo: List<HobbyItemResponse>
)

data class HobbyItemResponse(
    @SerializedName("hobbyInfoId")
    val hobbyInfoId: Long,
    @SerializedName("hobbyName")
    val hobbyName: String,
    @SerializedName("hobbyDescription")
    val hobbyDescription: String,
    @SerializedName("imageCode")
    val imageCode: String
) : RemoteMapper<HobbyItemEntity> {
    override fun toData(): HobbyItemEntity {
        return HobbyItemEntity(
            id = hobbyInfoId,
            name = hobbyName,
            description = hobbyDescription,
            imageCode = imageCode
        )
    }
}