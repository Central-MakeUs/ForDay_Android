package com.forday.app.domain.model

data class GuestLoginDomain(
    val status: Int,
    val isSuccess: Boolean,
    val data: GuestLoginDataDomain
)

data class GuestLoginDataDomain(
    val accessToken: String,
    val refreshToken: String,
    val isNewUser: Boolean,
    val socialType: String,
    val userId: String,
    val onboardingCompleted: Boolean,
    val nicknameSet: Boolean,
    val onboardingData: OnboardingDataDomain?,
    val nickname: String?
)

//data class OnboardingDataDomain(
//    val hobbyId: Long?,
//    val hobbyInfoId: Int?,
//    val hobbyName: String?,
//    val hobbyPurpose: String?,
//    val hobbyTimeMinutes: Int?,
//    val executionCount: Int?,
//    val durationSet: Boolean?
//)