package com.forday.app.domain.usecase

import com.forday.app.domain.repository.AuthRepository
import javax.inject.Inject

class SaveHasSeenIntroUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(hasSeen: Boolean) = authRepository.saveHasSeenIntro(hasSeen)
}
