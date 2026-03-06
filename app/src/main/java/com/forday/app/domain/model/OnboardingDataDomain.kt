package com.forday.app.domain.model

data class OnboardingDataDomain(
    val hobbyId: Long?,
    val hobbyInfoId: Int?,
    val hobbyName: String?,
    val hobbyPurpose: String?,
    val hobbyTimeMinutes: Int?,
    val executionCount: Int?,
    val durationSet: Boolean?
)