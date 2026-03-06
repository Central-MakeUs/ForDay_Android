package com.forday.app.remote.model.response

import com.forday.app.data.model.HobbyCardEntity
import com.forday.app.data.model.HobbyEntity
import com.forday.app.data.model.HobbyTabEntity
import com.forday.app.remote.RemoteMapper
import com.google.gson.annotations.SerializedName

/**
 * 취미 카드 목록 조회 API 응답
 */
data class HobbyCardResponse(
    @SerializedName("status")
    val status: Int,

    @SerializedName("success")
    val success: Boolean,

    @SerializedName("data")
    val data: HobbyCardData
) : RemoteMapper<HobbyCardEntity> {

    override fun toData(): HobbyCardEntity {
        return HobbyCardEntity(
            appVersion = data.appVersion,
            hobbies = data.hobbyInfo.orEmpty().map { it.toData() }
        )
    }
}

/**
 * 취미 카드 데이터
 */
data class HobbyCardData(
    @SerializedName("appVersion")
    val appVersion: String,

    @SerializedName("hobbyInfo")
    val hobbyInfo: List<HobbyCard>?
)

/**
 * 취미 카드 정보
 */
data class HobbyCard(
    @SerializedName("hobbyInfoId")
    val hobbyCardId: Long?,

    @SerializedName("hobbyName")
    val hobbyName: String,

    @SerializedName("hobbyDescription")
    val hobbyDescription: String,

    @SerializedName("imageCode")
    val imageCode: String
) : RemoteMapper<HobbyEntity> {

    override fun toData(): HobbyEntity {
        return HobbyEntity(
            id = hobbyCardId,
            name = hobbyName,
            description = hobbyDescription,
            imageCode = imageCode
        )
    }
}