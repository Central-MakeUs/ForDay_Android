package com.forday.app.domain.usecase

import com.forday.app.domain.repository.AuthRepository
import javax.inject.Inject

class GetIsOnboardingCompletedUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    operator fun invoke() = repository.getIsOnboardingCompleted()
}