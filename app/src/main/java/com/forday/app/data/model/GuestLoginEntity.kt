package com.forday.app.data.model

import com.forday.app.data.DataMapper
import com.forday.app.domain.model.GuestLoginDataDomain
import com.forday.app.domain.model.GuestLoginDomain
import com.forday.app.domain.model.OnboardingDataDomain

data class GuestLoginEntity(
    val status: Int,
    val isSuccess: Boolean,
    val data: GuestLoginDataEntity
): DataMapper<GuestLoginDomain> {
    override fun toDomain(): GuestLoginDomain {
        return GuestLoginDomain(
            status = status,
            isSuccess = isSuccess,
            data = data.toDomain()
        )
    }
}

data class GuestLoginDataEntity(
    val accessToken: String,
    val refreshToken: String,
    val isNewUser: Boolean,
    val socialType: String,
    val userId: String,
    val onboardingCompleted: Boolean,
    val nicknameSet: Boolean,
    val onboardingData: GuestOnboardingDataEntity?,
    val nickname: String?
) : DataMapper<GuestLoginDataDomain> {
    override fun toDomain(): GuestLoginDataDomain {
        return GuestLoginDataDomain(
            accessToken = accessToken,
            refreshToken = refreshToken,
            isNewUser = isNewUser,
            socialType = socialType,
            userId = userId,
            onboardingCompleted = onboardingCompleted,
            nicknameSet = nicknameSet,
            onboardingData = onboardingData?.toDomain(),
            nickname = nickname
        )
    }
}

data class GuestOnboardingDataEntity(
    val id: Long,
    val hobbyInfoId: Int,
    val hobbyName: String,
    val hobbyPurpose: String,
    val hobbyTimeMinutes: Int,
    val executionCount: Int,
    val durationSet: Boolean
): DataMapper<OnboardingDataDomain> {
    override fun toDomain(): OnboardingDataDomain {
        return OnboardingDataDomain(
            hobbyId = id,
            hobbyInfoId = hobbyInfoId,
            hobbyName = hobbyName,
            hobbyPurpose = hobbyPurpose,
            hobbyTimeMinutes = hobbyTimeMinutes,
            executionCount = executionCount,
            durationSet = durationSet
        )
    }
}