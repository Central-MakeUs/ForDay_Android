package com.forday.app.domain.usecase

import com.forday.app.domain.repository.AuthRepository
import javax.inject.Inject

class SaveIsNicknameSetUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(isNicknameSet: Boolean) =
        authRepository.saveIsNicknameSet(isNicknameSet)
}