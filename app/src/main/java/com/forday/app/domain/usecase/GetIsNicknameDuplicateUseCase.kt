package com.forday.app.domain.usecase

import com.forday.app.domain.repository.UserRepository
import javax.inject.Inject

class GetIsNicknameDuplicateUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(nickname: String) = userRepository.checkNicknameDuplicate(nickname)
}