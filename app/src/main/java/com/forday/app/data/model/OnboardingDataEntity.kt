package com.forday.app.data.model

import com.forday.app.data.DataMapper
import com.forday.app.domain.model.OnboardingDataDomain

data class OnboardingDataEntity(
    val hobbyId: Long?,
    val hobbyInfoId: Int?,
    val hobbyName: String?,
    val hobbyPurpose: String?,
    val hobbyTimeMinutes: Int?,
    val executionCount: Int?,
    val durationSet: Boolean?
): DataMapper<OnboardingDataDomain> {
    override fun toDomain(): OnboardingDataDomain {
        return OnboardingDataDomain(
            hobbyId = hobbyId,
            hobbyInfoId = hobbyInfoId,
            hobbyName = hobbyName,
            hobbyPurpose = hobbyPurpose,
            hobbyTimeMinutes = hobbyTimeMinutes,
            executionCount = executionCount,
            durationSet = durationSet
        )
    }
}