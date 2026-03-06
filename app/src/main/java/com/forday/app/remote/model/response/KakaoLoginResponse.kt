package com.forday.app.remote.model.response

import com.forday.app.data.model.KakaoLoginDataEntity
import com.forday.app.data.model.KakaoLoginEntity
import com.forday.app.data.model.KakaoOnboardingDataEntity
import com.forday.app.remote.RemoteMapper
import com.google.gson.annotations.SerializedName


data class KakaoLoginResponse(
    @SerializedName("status")
    val status: Int,

    @SerializedName("success")
    val success: Boolean,

    @SerializedName("data")
    val data: LoginData
) : RemoteMapper<KakaoLoginEntity> {
    override fun toData(): KakaoLoginEntity {
        return KakaoLoginEntity(
            status = status,
            isSuccess = success,
            data = data.toData()
        )
    }
}

data class LoginData(
    @SerializedName("accessToken")
    val accessToken: String,

    @SerializedName("refreshToken")
    val refreshToken: String,

    @SerializedName("newUser")
    val newUser: Boolean,

    @SerializedName("socialType")
    val socialType: String,

    @SerializedName("onboardingCompleted")
    val onboardingCompleted: Boolean,

    @SerializedName("nicknameSet")
    val nicknameSet: Boolean,

    // 추가된 필드: 모든 응답 케이스에 포함되어 있으므로 String으로 정의
    @SerializedName("nickname")
    val nickname: String? = null,

    // 선택적 필드 (Nullable 유지)
    @SerializedName("guestUserId")
    val guestUserId: String? = null,

    @SerializedName("onboardingData")
    val onboardingData: KakaoOnboardingData? = null
) : RemoteMapper<KakaoLoginDataEntity> {
    override fun toData(): KakaoLoginDataEntity {
        return KakaoLoginDataEntity(
            accessToken = accessToken,
            refreshToken = refreshToken,
            isNewUser = newUser,
            socialType = socialType,
            isOnboardingCompleted = onboardingCompleted,
            isNicknameSet = nicknameSet,
            nickname = nickname, // Entity에도 추가가 필요합니다.
            guestUserId = guestUserId,
            onboardingData = onboardingData?.toData()
        )
    }
}

data class KakaoOnboardingData(
    @SerializedName("id")
    val id: Int,

    @SerializedName("hobbyInfoId")
    val hobbyCardId: Int,

    @SerializedName("hobbyName")
    val hobbyName: String,

    @SerializedName("hobbyPurpose")
    val hobbyPurpose: String,

    @SerializedName("hobbyTimeMinutes")
    val hobbyTimeMinutes: Int,

    @SerializedName("executionCount")
    val executionCount: Int,

    @SerializedName("durationSet")
    val durationSet: Boolean
) : RemoteMapper<KakaoOnboardingDataEntity> {
    override fun toData(): KakaoOnboardingDataEntity {
        return KakaoOnboardingDataEntity(
            id = id,
            hobbyCardId = hobbyCardId,
            hobbyName = hobbyName,
            hobbyPurpose = hobbyPurpose,
            hobbyTimeMinutes = hobbyTimeMinutes,
            executionCount = executionCount,
            durationSet = durationSet
        )
    }
}