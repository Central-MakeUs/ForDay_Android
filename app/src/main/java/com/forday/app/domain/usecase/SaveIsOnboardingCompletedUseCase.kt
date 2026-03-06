package com.forday.app.domain.usecase

import com.forday.app.domain.repository.AuthRepository
import javax.inject.Inject

class SaveIsOnboardingCompletedUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(isOnboardingCompleted: Boolean) =
        authRepository.saveIsOnboardingCompleted(isOnboardingCompleted)
}