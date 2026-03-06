package com.forday.app.domain.usecase

import com.forday.app.domain.repository.UserRepository
import javax.inject.Inject

class RemoveOnboardingDataUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke() {
        userRepository.removeOnboardingData()
    }
}
