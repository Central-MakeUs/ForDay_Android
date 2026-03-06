package com.forday.app.domain.usecase

import com.forday.app.domain.repository.UserRepository
import timber.log.Timber
import javax.inject.Inject

class SaveOnboardingDataUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(
        hobbyCardId: Long?,
        hobbyName: String?,
        hobbyTimeMinutes: Int?,
        hobbyPurposes: String?,
        executionCount: Int?,
        isDurationSet: Boolean
    ) {
        Timber.e("@@@@@@@@@@@@@@@saveOnboardingDataUseCase "+hobbyCardId+", "+hobbyName+", "+hobbyTimeMinutes+", "+hobbyPurposes+", "+executionCount+", "+isDurationSet)
        userRepository.saveOnboardingData(
            selectedHobbyId = hobbyCardId,
            selectedHobbyName = hobbyName,
            selectedMinutes = hobbyTimeMinutes,
            selectedPurpose = hobbyPurposes,
            selectedFrequency = executionCount,
            selectedPeriod = isDurationSet
        )
    }
}