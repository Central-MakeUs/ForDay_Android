package com.forday.app.remote.model.response

import com.forday.app.data.model.GuestLoginDataEntity
import com.forday.app.data.model.GuestLoginEntity
import com.forday.app.data.model.GuestOnboardingDataEntity
import com.forday.app.remote.RemoteMapper
import com.google.gson.annotations.SerializedName

data class GuestLoginResponse(
    @SerializedName("status")
    val status: Int,

    @SerializedName("success")
    val isSuccess: Boolean,

    @SerializedName("data")
    val data: GuestLoginData
) : RemoteMapper<GuestLoginEntity> {
    override fun toData(): GuestLoginEntity {
        return GuestLoginEntity(
            status = status,
            isSuccess = isSuccess,
            data = data.toData()
        )
    }
}

data class GuestLoginData(
    @SerializedName("accessToken")
    val accessToken: String,

    @SerializedName("refreshToken")
    val refreshToken: String,

    @SerializedName("newUser")
    val isNewUser: Boolean,

    @SerializedName("socialType")
    val socialType: String,

    @SerializedName("guestUserId")
    val userId: String,

    @SerializedName("onboardingCompleted")
    val onboardingCompleted: Boolean,

    @SerializedName("nicknameSet")
    val nicknameSet: Boolean,

    @SerializedName("onboardingData")
    val onboardingData: GuestOnboardingData?,

    @SerializedName("nickname")
    val nickname: String?
) : RemoteMapper<GuestLoginDataEntity> {

    override fun toData(): GuestLoginDataEntity {
        return GuestLoginDataEntity(
            accessToken = accessToken,
            refreshToken = refreshToken,
            isNewUser = isNewUser,
            socialType = socialType,
            userId = userId,
            onboardingCompleted = onboardingCompleted,
            nicknameSet = nicknameSet,
            onboardingData = onboardingData?.toData(),
            nickname = nickname
        )
    }
}

data class GuestOnboardingData(
    @SerializedName("id")
    val id: Long,

    @SerializedName("hobbyInfoId")
    val hobbyInfoId: Int,

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
) : RemoteMapper<GuestOnboardingDataEntity> {
    override fun toData(): GuestOnboardingDataEntity {
        return GuestOnboardingDataEntity(
            id = id,
            hobbyInfoId = hobbyInfoId,
            hobbyName = hobbyName,
            hobbyPurpose = hobbyPurpose,
            hobbyTimeMinutes = hobbyTimeMinutes,
            executionCount = executionCount,
            durationSet = durationSet
        )
    }
}